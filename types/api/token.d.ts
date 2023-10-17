import type { Key } from "./shared";

export type TokenResponse = {
  admin_key: Key;
  auto_renew_account: string;
  auto_renew_period: number;
  created_timestamp: string;
  custom_fees: CustomFees;
  decimals: string;
  deleted: boolean;
  expiry_timestamp: number;
  fee_schedule_key: Key;
  freeze_default: boolean;
  freeze_key: Key;
  initial_supply: string;
  kyc_key: Key;
  max_supply: string;
  memo: string;
  modified_timestamp: string;
  name: string;
  pause_key: Key;
  pause_status: string;
  supply_key: Key;
  supply_type: "INFINITE" | "FINITE";
  symbol: string;
  token_id: string;
  total_supply: string;
  treasury_account_id: string;
  type: string; // possible values? "FUNGIBLE_COMMON" | "NON_FUNGIBLE_UNIQUE" | "ALL"
  wipe_key: Key;
};

interface CustomFees {
  created_timestamp: string;
  fixed_fees: FixedFee[];
  fractional_fees: FractionalFee[];
}

type FixedFee = {
  amount: number;
  collector_account_id: string;
  denominating_token_id: string;
};

type FractionalFee = {
  amount: Fraction;
  collector_account_id: string;
  denominating_token_id: string;
  maximum: number;
  minimum: number;
  net_of_transfers: boolean;
};

type Fraction = {
  numerator: number;
  denominator: number;
};
