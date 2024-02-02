import { TopicMessageSubmitTransaction } from '@hashgraph/sdk';

let customMockImplementation: (() => TopicMessageSubmitTransaction) | null =
  null;

export const setCustomMockImplementation = (
  customImpl: (() => TopicMessageSubmitTransaction) | null,
) => {
  customMockImplementation = customImpl;
};

const defaultMockImplementation = () =>
  ({
    freezeWith: jest.fn().mockReturnThis(),
    execute: jest.fn().mockResolvedValue({
      getReceipt: jest.fn().mockResolvedValue({
        topicSequenceNumber: 1,
      }),
    }),
  }) as unknown as TopicMessageSubmitTransaction;

const mockTopicMessageSubmitTransaction = () =>
  customMockImplementation
    ? customMockImplementation()
    : defaultMockImplementation();

const sdkMock = {
    setCustomMockImplementation,
    mockTopicMessageSubmitTransaction,
}

export default sdkMock;