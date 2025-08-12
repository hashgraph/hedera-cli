import { Logger } from '../utils/logger';
import { DomainError } from '../utils/errors';
import type { Account, Token, Topic, Script } from '../../types';
import { actions, getState } from './store';

const logger = Logger.getInstance();

/**
 * Add (or overwrite if allowed) an account in a transactional way.
 */
export function addAccount(account: Account, overwrite = false): Account {
  const storeAccounts = getState().accounts;
  if (!overwrite && storeAccounts[account.name]) {
    logger.error(`Account with name ${account.name} already exists`);
    throw new DomainError('Account already exists');
  }
  actions().addAccount(account, true /* validated */);
  return account;
}

/**
 * Remove an account by name.
 */
export function removeAccount(name: string): void {
  const storeAccounts = getState().accounts;
  if (!storeAccounts[name]) {
    logger.error(`Account with name ${name} not found`);
    throw new DomainError('Account not found');
  }
  actions().removeAccount(name);
}

export function addToken(token: Token, overwrite = false): Token {
  const existing = getState().tokens;
  if (!overwrite && existing[token.tokenId]) {
    logger.error(`Token with ID ${token.tokenId} already exists`);
    throw new DomainError('Token already exists');
  }
  actions().addToken(token, true);
  return token;
}

export function addTopic(topic: Topic, overwrite = false): Topic {
  const existing = getState().topics;
  if (!overwrite && existing[topic.topicId]) {
    logger.error(`Topic with ID ${topic.topicId} already exists`);
    throw new DomainError('Topic already exists');
  }
  actions().addTopic(topic, true);
  return topic;
}

export function addScript(script: Script, overwrite = false): Script {
  const internal = `script-${script.name}`;
  const existing = getState().scripts;
  if (!overwrite && existing[internal]) {
    logger.error(`Script with name ${script.name} already exists`);
    throw new DomainError('Script already exists');
  }
  actions().addScript(script, true);
  return script;
}

export default {
  addAccount,
  removeAccount,
  addToken,
  addTopic,
  addScript,
};
