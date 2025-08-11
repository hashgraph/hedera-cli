import { topicState, topic, baseState } from '../../helpers/state';
import commands from '../../../src/commands';
import { saveState as storeSaveState } from '../../../src/state/store';
import { Command } from 'commander';
import sdkMock from '../../helpers/sdk';
import { TopicMessageSubmitTransaction } from '@hashgraph/sdk';

jest.mock('../../../src/state/state'); // Mock the original module -> looks for __mocks__/state.ts in same directory
// Mock the @hashgraph/sdk module directly in the test file
jest.mock('@hashgraph/sdk', () => {
  const originalModule = jest.requireActual('@hashgraph/sdk');

  return {
    ...originalModule,
    TopicMessageSubmitTransaction: jest
      .fn()
      .mockImplementation(() => sdkMock.mockTopicMessageSubmitTransaction()),
  };
});

describe('topic message submit command', () => {
  const logSpy = jest.spyOn(console, 'log');

  beforeEach(() => {
  storeSaveState(topicState as any);
    sdkMock.setCustomMockImplementation(null);
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
      const message = 'Hello world!';

      // Overwrite the mock implementation of TopicCreateTransaction to return a sequence number of 1
      const sequenceNumber = 2;
      sdkMock.setCustomMockImplementation(() => ({
        freezeWith: jest.fn().mockReturnThis(),
        execute: jest.fn().mockResolvedValue({
          getReceipt: jest.fn().mockResolvedValue({
            topicSequenceNumber: sequenceNumber,
          }),
        }),
      }) as unknown as TopicMessageSubmitTransaction);

      // Act
      await program.parseAsync([
        'node',
        'hedera-cli.ts',
        'topic',
        'message',
        'submit',
        '--message',
        message,
        '-t',
        topic.topicId,
      ]);

      // Assert
      expect(logSpy).toHaveBeenCalledWith(
        `Message submitted with sequence number: ${sequenceNumber}`,
      );
    });
  });
});
