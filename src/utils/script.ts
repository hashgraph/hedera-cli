import { updateState as storeUpdateState } from '../state/store';
import { selectScripts } from '../state/selectors';
import { Logger } from './logger';
import { DomainError } from './errors';

const logger = Logger.getInstance();

function listScripts(): void {
  const scripts = selectScripts();
  const scriptNames = Object.keys(scripts);
  if (scriptNames.length === 0) {
    logger.log('No scripts found');
    return;
  }
  logger.log('Scripts:');
  scriptNames.forEach((key) => {
    const internal = key.startsWith('script-') ? key : `script-${key}`;
    const entry = scripts[key];
    logger.log(`\t${internal}`);
    logger.log(`\t- Commands:`);
    (entry.commands || []).forEach((command) => logger.log(`\t\t${command}`));
  });
}

function deleteScript(name: string): void {
  const key = `script-${name}`;
  const scripts = selectScripts();
  if (!scripts[key]) throw new DomainError(`No script found with name: ${key}`);
  storeUpdateState((draft) => {
    delete draft.scripts[key];
  });
  logger.log(`Script ${key} deleted successfully`);
}

const scriptUtils = {
  listScripts,
  deleteScript,
};

export default scriptUtils;
