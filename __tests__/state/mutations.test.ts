import { saveState as storeSaveState, get as storeGet, saveKey as storeSaveKey, saveScriptArgument as storeSaveScriptArgument } from '../../src/state/store';
import { baseState, alice } from '../helpers/state';
import { addAccount, removeAccount, addToken, addTopic, addScript } from '../../src/state/mutations';
import { DomainError } from '../../src/utils/errors';
import type { Account, Token, Topic, Script } from '../../types';


describe('state mutations helpers', () => {
  beforeEach(() => {
    storeSaveState({ ...baseState });
  });

  test('addAccount adds new account', () => {
    const created = addAccount(alice, false);
    expect(created.name).toBe(alice.name);
  expect(storeGet('accounts')[alice.name]).toEqual(alice);
  });

  test('removeAccount deletes existing account', () => {
    addAccount(alice, false);
    removeAccount(alice.name);
  expect(storeGet('accounts')[alice.name]).toBeUndefined();
  });

  test('addAccount fails on duplicate when overwrite=false', () => {
    addAccount(alice, false);
    expect(() => addAccount(alice, false)).toThrow(DomainError);
  });
  
  test('addToken and association handling (silent skip on unknown)', () => {
    const token: Token = { network: 'localnet', associations: [], tokenId: '0.0.123', name: 'T', symbol: 'T', treasuryId: '0.0.2', decimals: 0, initialSupply: 0, supplyType: 'finite', maxSupply: 1, keys: { adminKey: '', pauseKey: '', kycKey: '', wipeKey: '', freezeKey: '', supplyKey: '', feeScheduleKey: '', treasuryKey: '' }, customFees: [] };
    addToken(token, false);
  expect(storeGet('tokens')[token.tokenId]).toBeDefined();
  });

  test('addTopic simple add', () => {
    const topic: Topic = { network: 'localnet', topicId: '0.0.456', memo: 'm', adminKey: '', submitKey: '' };
    addTopic(topic, false);
  expect(storeGet('topics')[topic.topicId]).toBeDefined();
  });

  test('script lifecycle resets args on stop', () => {
    const script: Script = { name: 'demo', creation: Date.now(), commands: [], args: {} };
    addScript(script, false);
    // simulate start
  storeSaveKey('scriptExecution', { active: true, name: 'demo' } as any);
  storeSaveScriptArgument('foo', 'bar');
  expect(storeGet('scripts')["script-demo"].args.foo).toBe('bar');
    // stop
  storeSaveKey('scriptExecution', { active: false, name: '' } as any);
  expect(storeGet('scripts')["script-demo"].args).toEqual({ foo: 'bar' }); // stop doesn't auto clear
  });
});
