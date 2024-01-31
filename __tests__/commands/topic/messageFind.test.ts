import { topicState, topic, baseState } from '../../helpers/state';
import commands from '../../../src/commands';
import stateController from '../../../src/state/stateController';
import { Command } from 'commander';
import api from '../../../src/api';
import { findMessageResponseMock, topicResponse } from '../../helpers/api/apiTopicHelper'

jest.mock('../../../src/state/state'); // Mock the original module -> looks for __mocks__/state.ts in same directory


describe('topic message submit command', () => {
  const logSpy = jest.spyOn(console, 'log');

  beforeEach(() => {
    stateController.saveState(topicState);
  });

  describe('topic message submit - success path', () => {
    afterEach(() => {
      // Spy cleanup
      logSpy.mockClear();
    });

    test('✅ Submit message to topic ID', async () => {
      // Arrange
      const program = new Command();
      commands.topicCommands(program);
      api.topic.findMessage = jest.fn().mockResolvedValue(findMessageResponseMock);
      
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
        topicResponse.sequence_number.toString(),
      ]);

      // Assert
      expect(logSpy).toHaveBeenCalledWith(
        `Message found: "${Buffer.from(
            topicResponse.message,
            'base64',
          ).toString('ascii')}"`
      );
    });
  });
});
