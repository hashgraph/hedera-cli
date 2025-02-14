import stateController from '../state/stateController';
import { Logger } from '../utils/logger';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('telemetry <action>')
    .description('Enable or disable telemetry')
    .action((action: string) => {
      switch (action) {
        case 'enable':
          stateController.saveKey('telemetry', 1);
          logger.log('Telemetry turned on');
          break;
        case 'disable':
          stateController.saveKey('telemetry', 0);
          logger.log('Telemetry turned off');
          break;
        default:
          logger.error(
            `Unknown telemetry option: Use 'enable' or 'disable' commands`,
          );
          process.exit(1);
      }
    });
};
