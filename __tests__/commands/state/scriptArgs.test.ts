import {
  saveState as storeSaveState,
  saveKey as storeSaveKey,
  saveScriptArgument as storeSaveScriptArgument,
  getScriptArgument as storeGetScriptArgument,
  get as storeGet,
} from '../../../src/state/store';
import { baseState } from '../../helpers/state';

/**
 * Behavior lock tests for saveScriptArgument & getScriptArgument.
 */

describe('script argument helpers', () => {
  beforeEach(() => {
    storeSaveState({
      ...baseState,
      scriptExecution: { active: false, name: '' },
      scripts: {},
    } as any);
  });

  test('does not save when no script executing', () => {
    storeSaveScriptArgument('foo', 'bar');
    expect(storeGetScriptArgument('foo')).toBeUndefined();
    expect(Object.keys(storeGet('scripts' as any))).toHaveLength(0);
  });

  test('saves and retrieves during active script execution', () => {
    storeSaveKey(
      'scriptExecution' as any,
      { active: true, name: 'basic' } as any,
    );
    storeSaveKey('scripts' as any, {
      'script-basic': {
        name: 'basic',
        creation: Date.now(),
        commands: [],
        args: {},
      },
    });

    storeSaveScriptArgument('foo', 'bar');
    expect(storeGetScriptArgument('foo')).toBe('bar');
  });

  test('multiple arguments accumulate in same script entry', () => {
    storeSaveKey(
      'scriptExecution' as any,
      { active: true, name: 'basic' } as any,
    );
    storeSaveKey('scripts' as any, {
      'script-basic': {
        name: 'basic',
        creation: Date.now(),
        commands: [],
        args: {},
      },
    });

    storeSaveScriptArgument('first', '1');
    storeSaveScriptArgument('second', '2');
    const scripts = storeGet('scripts' as any);
    expect(scripts['script-basic'].args).toEqual({ first: '1', second: '2' });
  });
});
