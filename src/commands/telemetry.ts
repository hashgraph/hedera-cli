import { saveKey as storeSaveKey } from '../state/store';
import { Logger } from '../utils/logger';
import { DomainError, exitOnError } from '../utils/errors';
import { Command } from 'commander';

const logger = Logger.getInstance();

export default (program: Command) => {
  program
    .command('telemetry <action>')
    .description('Enable or disable telemetry')
    .action(
      exitOnError((action: string) => {
        if (action === 'enable') {
          storeSaveKey('telemetry', 1);
          logger.log('Telemetry turned on');
          return;
        }
        if (action === 'disable') {
          storeSaveKey('telemetry', 0);
          logger.log('Telemetry turned off');
          return;
        }
        throw new DomainError(
          "Unknown telemetry option: Use 'enable' or 'disable' commands",
        );
      }),
    );
};
