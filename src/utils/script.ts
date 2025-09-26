import { selectScripts } from '../state/selectors';
import { updateState as storeUpdateState } from '../state/store';
import { color, heading } from './color';
import { DomainError } from './errors';
import { Logger } from './logger';
import { isJsonOutput, printOutput } from './output';

const logger = Logger.getInstance();

function listScripts(): void {
  const scripts = selectScripts();
  const scriptNames = Object.keys(scripts);
  if (isJsonOutput()) {
    printOutput('scripts', {
      scripts: scriptNames.map((key) => {
        const entry = scripts[key];
        return {
          name: key.replace(/^script-/, ''),
          commands: entry.commands,
          creation: entry.creation,
        };
      }),
    });
    return;
  }
  if (scriptNames.length === 0) {
    logger.log(heading('No scripts found'));
    return;
  }
  logger.log(heading('Scripts:'));
  scriptNames.forEach((key) => {
    const internal = key.startsWith('script-') ? key : `script-${key}`;
    const entry = scripts[key];
    logger.log(`\t${color.magenta(internal)}`);
    logger.log(`\t- Commands:`);
    (entry.commands || []).forEach((command) =>
      logger.log(`\t\t${color.cyan(command)}`),
    );
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
