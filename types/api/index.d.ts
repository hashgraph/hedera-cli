import type { AccountResponse } from './account.d.ts';
import type { TokenResponse, BalanceResponse } from './token.d.ts';
import type { TopicResponse } from './topic.d.ts';

type APIResponseTypes =
  | AccountResponse
  | TokenResponse
  | BalanceResponse
  | TopicResponse;
export type APIResponse<T extends APIResponseTypes = APIResponseTypes> = {
  data: T;
};

export type * from './account.d.ts';
export type * from './token.d.ts';
export type * from './topic.d.ts';
export type * from './shared.d.ts';
