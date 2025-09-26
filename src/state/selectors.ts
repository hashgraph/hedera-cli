import type { Account, Script, Token, Topic } from '../../types';
import { getState } from './store';
import type { StoreState } from './store';

export const selectState = (): StoreState => getState();
export const selectNetwork = (): StoreState['network'] => getState().network;
export const selectNetworks = (): StoreState['networks'] => getState().networks;
// Simple selectors (no custom memo layer) for clarity & idiomatic vanilla Zustand usage
export const selectTelemetry = (): {
  telemetry: number;
  telemetryServer: string;
} => {
  const s = getState();
  return { telemetry: s.telemetry, telemetryServer: s.telemetryServer };
};

export const selectAccounts = (): Record<string, Account> =>
  getState().accounts;
export const selectTokens = (): Record<string, Token> => getState().tokens;
export const selectTopics = (): Record<string, Topic> => getState().topics;
export const selectScripts = (): Record<string, Script> => getState().scripts;

// Entity lookup helpers
export const selectAccountByName = (name: string): Account | undefined =>
  selectAccounts()[name];
export const selectTokenById = (tokenId: string): Token | undefined =>
  selectTokens()[tokenId];
export const selectTopicById = (topicId: string): Topic | undefined =>
  selectTopics()[topicId];
export const selectScriptByInternalName = (
  internal: string,
): Script | undefined => selectScripts()[internal];

// Derived helpers
export const selectAccountNames = (): string[] => Object.keys(selectAccounts());
export const selectTokenIds = (): string[] => Object.keys(selectTokens());
export const selectTopicIds = (): string[] => Object.keys(selectTopics());

export const selectScriptArgument = (argument: string): string | undefined => {
  const s = getState();
  if (!s.scriptExecution.active) return undefined;
  const key = `script-${s.scriptExecution.name}`;
  return s.scripts[key]?.args?.[argument];
};
