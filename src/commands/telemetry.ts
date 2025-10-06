import { Command } from 'commander';
import { saveKey as storeSaveKey } from '../state/store';
import { DomainError } from '../utils/errors';
import { Logger } from '../utils/logger';
import { wrapAction } from './shared/wrapAction';

const logger = Logger.getInstance();

export default (program: Command) => {
  program
    .command('telemetry <action>')
    .alias('tel')
    .description('Enable or disable telemetry')
    .action(
      wrapAction(
        ({ action }: { action: string }) => {
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
        },
        {
          log: 'Updating the telemetry state where providing true will generate an anonymous user UUID and start tracking the commands executed)',
        },
      ),
    );
};
