function displayHbarBalance(accountId, hbars) {
  console.log(`Hbar Balance for account ${accountId}:`);
  console.log(`${hbars} Hbars`);
}

function displayTokenBalance(accountId, tokens, tokenId) {
  const tokenBalance = tokens.find((token) => token.token_id === tokenId);
  if (tokenBalance) {
    console.log(`Token Balance for account ${accountId}:`);
    console.log(`Token ID ${tokenId}: ${tokenBalance.balance}`);
  } else {
    console.log(
      `No balance found for token ID ${tokenId} in account ${accountId}`
    );
  }
}

function displayAllBalances(accountId, hbars, tokens) {
  console.log(`Balance for account ${accountId}:`);
  console.log(`${hbars} Hbars\n`);

  if (tokens && tokens.length > 0) {
    console.log("Token balances:");
    tokens.forEach((token) => {
      console.log(`${token.token_id}: ${token.balance}`);
    });
  }
}

// In your action or function where you have the response and options:
function displayBalances(response, options) {
  const accountId = response.data.account;
  const hbars = response.data.balance.balance;
  const tokens = response.data.balance.tokens;

  if (options.onlyHbar) {
    return displayHbarBalance(accountId, hbars);
  }

  if (options.tokenId) {
    return displayTokenBalance(accountId, tokens, options.tokenId);
  }

  return displayAllBalances(accountId, hbars, tokens);
}

module.exports = {
    displayBalances,
};