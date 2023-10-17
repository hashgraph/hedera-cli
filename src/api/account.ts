import axios from "axios";
import type { APIResponse } from "../../types";
import { getMirrorNodeURL } from "../state/stateService";

/**
 * API functions:
 * - getAccountBalance(accountId): Get the balance of an account
 */

async function getAccountBalance(accountId: string): Promise<APIResponse> {
  try {
    const mirrorNodeURL = getMirrorNodeURL();
    const response = await axios.get(`${mirrorNodeURL}/accounts/${accountId}`);
    return response;
  } catch (error) {
    if (axios.isAxiosError(error)){
      throw new Error(`Error fetching account balance: ${error.message}`);
    } else {
      throw new Error(`Unexpected error: ${error}`)
    }
  }
}

export default {
  getAccountBalance,
};