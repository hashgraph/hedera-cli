import axios from 'axios';

import { Command } from 'commander';
import api from '../../../src/api';
import commands from '../../../src/commands';
import { saveState as storeSaveState } from '../../../src/state/store';
import { Logger } from '../../../src/utils/logger';
import stateUtils from '../../../src/utils/state';
import {
  findMessageResponseMock,
  topicMessageResponse,
  topicMessagesResponse,
} from '../../helpers/api/apiTopicHelper';
import { topic, topicState } from '../../helpers/state';

const logger = Logger.getInstance();
jest.mock('axios');

describe('topic message find command', () => {
  const logSpy = jest.spyOn(logger, 'log');
  const errorSpy = jest.spyOn(logger, 'error');

  beforeEach(() => {
    storeSaveState(topicState as any);
  });

  describe('topic message find - success path', () => {
    afterEach(() => {
      // Spy cleanup
      logSpy.mockClear();
      errorSpy.mockClear();
    });

    test('✅ Find message for topic ID and sequence number', async () => {
      // Arrange
      const program = new Command();
      commands.topicCommands(program);
      api.topic.findMessage = jest
        .fn()
        .mockResolvedValue(findMessageResponseMock);

      // Act
      await program.parseAsync([
        'node',
        'hedera-cli.ts',
        'topic',
        'message',
        'find',
        '--topic-id',
        topic.topicId,
        '--sequence-number',
        topicMessageResponse.sequence_number.toString(),
      ]);

      // Assert
      expect(logSpy).toHaveBeenCalledWith(
        `Message found: "${Buffer.from(
          topicMessageResponse.message,
          'base64',
        ).toString('ascii')}"`,
      );
    });

    test('✅ Find message for topic ID and sequence number filters', async () => {
      // Arrange
      const program = new Command();
      commands.topicCommands(program);
      const mockedAxios = axios as jest.Mocked<typeof axios>;
      const mockResponse = { data: topicMessagesResponse };
      mockedAxios.get.mockResolvedValue(mockResponse);

      // Act
      await program.parseAsync([
        'node',
        'hedera-cli.ts',
        'topic',
        'message',
        'find',
        '--topic-id',
        topic.topicId,
        '--sequence-number-gte',
        topicMessagesResponse.messages.length.toString(),
      ]);

      // Assert
      expect(mockedAxios.get).toHaveBeenCalledWith(
        `${stateUtils.getMirrorNodeURL()}/topics/${topic.topicId}/messages?sequencenumber=gte:${topicMessagesResponse.messages.length.toString()}&limit=100`,
      );
    });
  });

  describe('topic message find - failing path', () => {
    afterEach(() => {
      // Spy cleanup
      logSpy.mockClear();
      errorSpy.mockClear();
    });

    test('❌ If no sequence number and sequence number filters are provided throw error', async () => {
      // Arrange
      const program = new Command();
      commands.topicCommands(program);
      api.topic.findMessage = jest
        .fn()
        .mockResolvedValue(findMessageResponseMock);

      // Act
      await program.parseAsync([
        'node',
        'hedera-cli.ts',
        'topic',
        'message',
        'find',
        '--topic-id',
        topic.topicId,
      ]);

      // Assert
      expect(errorSpy).toHaveBeenCalledWith(
        'Please provide a sequence number or a sequence number filter',
      );
    });

    test('❌ If no messages with filter are found, show "no messages found" message', async () => {
      // Arrange
      const program = new Command();
      commands.topicCommands(program);
      const customFindMessageResponseMock = {
        data: {
          messages: [],
          links: {
            next: null,
          },
        },
      };
      api.topic.findMessagesWithFilters = jest
        .fn()
        .mockResolvedValue(customFindMessageResponseMock);

      // Act
      await program.parseAsync([
        'node',
        'hedera-cli.ts',
        'topic',
        'message',
        'find',
        '--topic-id',
        topic.topicId,
        '--sequence-number-gte',
        '1000000',
      ]);

      // Assert
      expect(logSpy).toHaveBeenCalledWith('No messages found');
    });
  });
});
