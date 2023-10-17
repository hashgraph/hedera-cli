import type { Key } from "./shared";

interface CustomFees {
  created_timestamp: string;
  fixed_fees: any[];
  fractional_fees: any[];
}

export type TokenResponse = {
  admin_key: Key;
  auto_renew_account: string;
  auto_renew_period: number;
  created_timestamp: string;
  custom_fees: CustomFees;
  decimals: string;
  deleted: boolean;
  expiry_timestamp: number;
  fee_schedule_key: Key; // If there are other possible values, you might want to adjust this type
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
  supply_type: "INFINITE" | "FINITE"; // Adjust if there are other possible values
  symbol: string;
  token_id: string;
  total_supply: string;
  treasury_account_id: string;
  type: string; // possible values? "FUNGIBLE_COMMON"
  wipe_key: Key;
};
