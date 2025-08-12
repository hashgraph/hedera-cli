import { AccountResponse, APIResponse } from '../../../types';

export const accountResponse: AccountResponse = {
  account: '0.0.1117',
  name: 'nameString',
  auto_renew_period: 7890,
  balance: {
    balance: 1000,
    timestamp: '1701089628.466331003',
    tokens: [{ token_id: '0.0.5678', balance: 200 }],
  },
  created_timestamp: '1690481295.691012650',
  decline_reward: false,
  deleted: false,
  ethereum_nonce: 0,
  evm_address: '0x000000000000000000000000000000000000045d',
  expiry_timestamp: '1698257295.691012650',
  key: {
    _type: 'ECDSA',
    key: '1c8434c89f76882bdb35e429b02b3eb4ba391c3e1869481b276819dd0e9c7d69',
  },
  max_automatic_token_associations: 0,
  memo: '',
  pending_reward: 0,
  receiver_sig_required: false,
  staked_account_id: null,
  staked_node_id: null,
  stake_period_start: null,
  transactions: [],
};

export const getAccountInfoResponseMock: APIResponse = {
  data: accountResponse,
};
