import { TopicResponse, APIResponse } from '../../../types';
import { topic } from '../../helpers/state'

export const topicResponse: TopicResponse = {
  chunk_info: {
    initial_transaction_id: {
      account_id: '0.0.458179',
      nonce: 0,
      scheduled: false,
      transaction_valid_start: '1706704154.157177180',
    },
    number: 1,
    total: 1,
  },
  consensus_timestamp: '1706704163.840322003',
  message: 'bXkgbWVzc2FnZQ==', // "my message"
  payer_account_id: '0.0.458179',
  running_hash:
    '+582vzqudSAftHP/xL21zS+1BwOlC/UGJW5K5Tb2I8wyeho54b7j5iNy4Ap//arW',
  running_hash_version: 3,
  sequence_number: 1,
  topic_id: topic.topicId,
};

export const findMessageResponseMock: APIResponse = {
  data: topicResponse,
};
