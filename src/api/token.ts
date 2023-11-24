import axios from 'axios';
import type { APIResponse } from "../../types";
import { getMirrorNodeURL } from "../state/stateService";

/**
 * 
 * API functions:
 * - getTokenInfo(tokenId): Get the info of a token
 * - getTokenBalance(tokenId, accountId): Get the balance of a token for an account
 */

async function getTokenInfo(tokenId: string): Promise<APIResponse> {
    try {
        const mirrorNodeURL = getMirrorNodeURL();
        const response = await axios.get(`${mirrorNodeURL}/tokens/${tokenId}`);
        return response;
    } catch (error) {
        if (axios.isAxiosError(error)) {
            throw new Error(`Error fetching token info: ${error.message}`);
        } else {
            throw new Error(`Unexpected error: ${error}`);
        }
    }
}

async function getTokenBalance(tokenId: string, accountId: string): Promise<APIResponse> {
    try {
        const mirrorNodeURL = getMirrorNodeURL();
        const response = await axios.get(`${mirrorNodeURL}/tokens/${tokenId}/balances?account.id=eq:${accountId}`);
        return response.data;
    } catch (error) {
        if (axios.isAxiosError(error)) {
            throw new Error(`Error fetching token balance: ${error.message}`);
        } else {
            throw new Error(`Unexpected error: ${error}`);
        }
    }
}

export default {
    getTokenInfo,
    getTokenBalance,
};
