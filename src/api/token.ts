import axios from 'axios';
import type { APIResponse } from "../../types";
import { getMirrorNodeURL } from "../state/stateService";

/**
 * 
 * API functions:
 * - getTokenInfo(tokenId): Get the info of a token
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

export default {
    getTokenInfo,
};
