import type { AccountResponse } from "./account.d.ts";
import type { TokenResponse, BalanceResponse } from "./token.d.ts";

type APIResponseTypes = AccountResponse | TokenResponse | BalanceResponse;
export type APIResponse<T extends APIResponseTypes = APIResponseTypes> = {
    data: T;
}

export type * from './account.d.ts';
export type * from './token.d.ts';