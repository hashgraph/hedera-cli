import type {
  APIResponse,
  AccountResponse,
  TokenBalance,
  DisplayBalanceOptions,
  DisplayOptions,
} from '../../types';
import { Logger } from './logger';

const logger = Logger.getInstance();

type DisplayFunction = (response: APIResponse, options?: any) => void;

const displayFunctions: Record<string, DisplayFunction> = {
  displayBalance: displayBalance,
};

// -- main display function -- //
function display(
  displayFunctionName: string,
  response: APIResponse,
  options: DisplayOptions,
): void {
  displayFunctions[displayFunctionName](response, options);
}

// -- display balance functions -- //
function displayHbarBalance(accountId: string, hbars: number): void {
  logger.log(`Hbar balance for account ${accountId}:`);
  logger.log(`${hbars} Tinybars or ${hbars / 100000000} Hbar`);
}

function displayTokenBalance(
  accountId: string,
  tokens: TokenBalance[],
  tokenId: string,
): void {
  const tokenBalance = tokens.find(
    (token: TokenBalance) => token.token_id === tokenId,
  );
  if (tokenBalance) {
    logger.log(`Token balance(s) for account ${accountId}:\n`);
    logger.log(`Token ID ${tokenId}: ${tokenBalance.balance}`);
  } else {
    logger.log(
      `No balance found for token ID ${tokenId} in account ${accountId}`,
    );
  }
}

function displayAllBalances(
  accountId: string,
  hbars: number,
  tokens: TokenBalance[],
): void {
  logger.log(`Balance for account ${accountId}:`);
  logger.log(`${hbars} Hbars\n`);

  if (tokens && tokens.length > 0) {
    logger.log('Token balances:');
    tokens.forEach((token: TokenBalance) => {
      logger.log(`${token.token_id}: ${token.balance}`);
    });
  }
}

function displayBalance(
  response: APIResponse,
  options: DisplayBalanceOptions,
): void {
  const accountResponse = response.data as AccountResponse;
  const accountId = accountResponse.account;
  const hbars = accountResponse.balance.balance;
  const tokens = accountResponse.balance.tokens;

  if (options.onlyHbar) {
    return displayHbarBalance(accountId, hbars);
  }

  if (options.tokenId) {
    return displayTokenBalance(accountId, tokens, options.tokenId);
  }

  return displayAllBalances(accountId, hbars, tokens);
}

export { display };
