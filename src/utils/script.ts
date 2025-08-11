import {
  get as storeGet,
  updateState as storeUpdateState,
} from '../state/store';
import { selectScripts } from '../state/selectors';

import type { Script } from '../../types';
import { Logger } from './logger';
import { DomainError } from './errors';

const logger = Logger.getInstance();

function listScripts() {
  const scripts = selectScripts();
  const scriptNames = Object.keys(scripts);

  if (scriptNames.length === 0) {
    logger.log('No scripts found');
    return;
  }

  logger.log('Scripts:');
  scriptNames.forEach((scriptName) => {
    // Ensure we log with internal name prefix as tests expect \tscript-<name>
    const internal = scriptName.startsWith('script-')
      ? scriptName
      : `script-${scriptName}`;
    logger.log(`\t${internal}`);
    logger.log(`\t- Commands:`);
    const commands =
      scripts[scriptName].commands && scripts[scriptName].commands.length
        ? scripts[scriptName].commands
        : (storeGet('scripts' as any) as any)[scriptName]?.commands || [];
    commands.forEach((command: string) => {
      logger.log(`\t\t${command}`);
    });
  });
}

function deleteScript(name: string) {
  const scriptName = `script-${name}`;
  let found = false;
  storeUpdateState((draft: any) => {
    if (!draft.scripts[scriptName]) {
      return; // handle after mutation
    }
    found = true;
    const { [scriptName]: _, ...rest } = draft.scripts as Record<
      string,
      Script
    >;
    draft.scripts = { ...rest } as Record<string, Script>;
  });
  if (!found) {
    throw new DomainError(`No script found with name: ${scriptName}`);
  }
  logger.log(`Script ${scriptName} deleted successfully`);
}

const scriptUtils = {
  listScripts,
  deleteScript,
};

export default scriptUtils;
