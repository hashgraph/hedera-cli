import type { AccountResponse } from "./account.d.ts";
import type { TokenResponse } from "./token.d.ts";

export type APIResponseTypes = AccountResponse | TokenResponse;

export type APIResponse = {
    data: APIResponseTypes;
}

export type * from './account.d.ts';
export type * from './token.d.ts';