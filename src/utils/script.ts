import stateController from '../state/stateController';

import type { Script } from '../../types';
import { Logger } from './logger';

const logger = Logger.getInstance();

function listScripts() {
  const scripts = stateController.get('scripts');
  const scriptNames = Object.keys(scripts);

  if (scriptNames.length === 0) {
    logger.log('No scripts found');
    return;
  }

  logger.log('Scripts:');
  scriptNames.forEach((scriptName) => {
    logger.log(`\t${scriptName}`);
    logger.log(`\t- Commands:`);
    scripts[scriptName].commands.forEach((command) => {
      logger.log(`\t\t${command}`);
    });
  });
}

function deleteScript(name: string) {
  const scripts = stateController.get('scripts');
  const scriptName = `script-${name}`;
  const script = scripts[scriptName];

  if (!script) {
    logger.error(`No script found with name: ${scriptName}`);
    process.exit(1);
  }

  delete scripts[scriptName];
  stateController.saveKey('scripts', scripts);
  logger.log(`Script ${scriptName} deleted successfully`);
}

const scriptUtils = {
  listScripts,
  deleteScript,
};

export default scriptUtils;
