const axios = require('axios');
const { getMirrorNodeURL } = require('../state/stateService');

/**
 * API functions:
 * - getAccountBalance(accountId): Get the balance of an account
 */

async function getAccountBalance(accountId) {
  try {
    const mirrorNodeURL = getMirrorNodeURL();
    const response = await axios.get(`${mirrorNodeURL}/accounts/${accountId}`);
    return response;
  } catch (error) {
    throw new Error(`Error fetching account balance: ${error.message}`);
  }
}

module.exports = {
  getAccountBalance,
};