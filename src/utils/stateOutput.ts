import type { StoreState } from '../state/store';
import { getState } from '../state/store';
import { printOutput } from './output';
import stateUtils from './state';

export interface StateViewOptions {
  all?: boolean;
  accounts?: boolean;
  accountId?: string;
  accountName?: string;
  tokens?: boolean;
  tokenId?: string;
  scripts?: boolean;
  topics?: boolean; // reserved for future flag parity
}

// Return public portion of the Zustand store (excluding actions)
export function getPublicState(): Omit<StoreState, 'actions'> {
  const { actions: _actions, ...rest } = getState();
  void _actions; // mark used to satisfy eslint
  return rest;
}

export function pickState(opts: StateViewOptions) {
  const full = getPublicState();
  if (opts.all) return full;
  const out: Record<string, unknown> = {};
  if (opts.accounts) out.accounts = full.accounts;
  if (opts.scripts) out.scripts = full.scripts;
  if (opts.tokens) out.tokens = full.tokens;
  if (opts.topics) out.topics = full.topics;
  if (opts.accountId) {
    out.account =
      stateUtils.getAccountById(opts.accountId) || 'Account not found';
  } else if (opts.accountName) {
    out.account = full.accounts[opts.accountName] || 'Account not found';
  }
  if (opts.tokenId) out.token = full.tokens[opts.tokenId] || 'Token not found';
  // If nothing selected return full
  if (Object.keys(out).length === 0) return full;
  return out;
}

export function outputState(tag: string, opts: StateViewOptions) {
  printOutput(tag, { state: pickState(opts) });
}
