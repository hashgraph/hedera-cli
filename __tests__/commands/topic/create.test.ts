import { topicState, topic, baseState } from '../../helpers/state';
import commands from '../../../src/commands';
import { saveState as storeSaveState, get as storeGet } from '../../../src/state/store';
import { Command } from 'commander';

jest.mock('../../../src/state/state'); // Mock the original module -> looks for __mocks__/state.ts in same directory
jest.mock('@hashgraph/sdk', () => {
  const originalModule = jest.requireActual('@hashgraph/sdk');

  return {
    ...originalModule,
    TopicCreateTransaction: jest.fn().mockImplementation(() => ({
      setTopicMemo: jest.fn().mockReturnThis(),
      setAdminKey: jest.fn().mockReturnThis(),
      setSubmitKey: jest.fn().mockReturnThis(),
      freezeWith: jest.fn().mockReturnThis(),
      execute: jest.fn().mockResolvedValue({
        getReceipt: jest.fn().mockResolvedValue({
          topicId: topic.topicId,
        })
      }),
    })),
  };
});

describe('topic create command', () => {
  const logSpy = jest.spyOn(console, 'log');

  beforeEach(() => {
  storeSaveState(baseState as any);
  });

  describe('topic create - success path', () => {
    afterEach(() => {
      // Spy cleanup
      logSpy.mockClear();
    });

    test('✅ Create a new topic', async () => {
      // Arrange
      const program = new Command();
      commands.topicCommands(program);
      const customTopic = {
        ...topic,
        memo: 'my custom memo',
      }

      // Act
      await program.parseAsync(['node', 'hedera-cli.ts', 'topic', 'create', '--memo', customTopic.memo]);

      // Assert
  const topics = storeGet('topics' as any);
      expect(Object.keys(topics).length).toEqual(1);
      expect(topics[topic.topicId]).toEqual(customTopic);
      expect(logSpy).toHaveBeenCalledWith(`Created new topic: ${topic.topicId}`);
    });
  });
});
