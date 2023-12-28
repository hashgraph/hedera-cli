import stateController from '../state/stateController';
import { Logger } from '../utils/logger';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('record <action> [name]')
    .description('Manage recording of a script')
    .action((action: string, name: string) => {
      switch (action) {
        case 'start':
          if (!name) {
            logger.error('Script name is required for start action');
            process.exit(1);
          }
          startRecording(name);
          logger.log(`Recording started for script: ${name}`);
          break;
        case 'stop':
          stopRecording();
          logger.log('Recording stopped');
          break;
        default:
          logger.error(`Unknown recording action called: ${action}`);
          process.exit(1);
      }
    });
};

function startRecording(scriptName: string) {
  const state = stateController.getAll();
  state.recording = 1;
  state.recordingScriptName = `script-${scriptName}`;
  state.scripts[state.recordingScriptName] = {
    name: scriptName,
    creation: Date.now(),
    commands: [],
    args: {},
  };
  stateController.saveState(state);
}

function stopRecording(): void {
  stateController.saveKey('recording', 0);
  stateController.saveKey('recordingScriptName', '');
}
