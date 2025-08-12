import { saveState as storeSaveState } from '../../src/state/store';
import { baseState, alice, token, topic, script_basic } from '../helpers/state';
import {
  selectAccounts,
  selectAccountByName,
  selectTokenById,
  selectTopicById,
  selectScriptByInternalName,
  selectTokenIds,
  selectTopicIds,
  selectAccountNames,
} from '../../src/state/selectors';

describe('state selectors', () => {
  beforeEach(() => {
    storeSaveState({
      ...baseState,
      accounts: { [alice.name]: alice },
      tokens: { [token.tokenId]: token },
      topics: { [topic.topicId]: topic },
      scripts: { [`script-${script_basic.name}`]: script_basic },
    } as any);
  });

  test('selectAccounts and selectAccountNames', () => {
    expect(selectAccounts()[alice.name]).toEqual(alice);
    expect(selectAccountNames()).toContain(alice.name);
  });

  test('entity selectors', () => {
    expect(selectAccountByName(alice.name)?.accountId).toBe(alice.accountId);
    expect(selectTokenById(token.tokenId)?.name).toBe(token.name);
    expect(selectTopicById(topic.topicId)?.memo).toBe(topic.memo);
    expect(
      selectScriptByInternalName(`script-${script_basic.name}`)?.name,
    ).toBe(script_basic.name);
  });

  test('id collections', () => {
    expect(selectTokenIds()).toContain(token.tokenId);
    expect(selectTopicIds()).toContain(topic.topicId);
  });
});
