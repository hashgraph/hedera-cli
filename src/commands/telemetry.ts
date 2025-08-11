import { saveKey as storeSaveKey } from '../state/store';
import { Logger } from '../utils/logger';
import { DomainError, exitOnError } from '../utils/errors';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('telemetry <action>')
    .description('Enable or disable telemetry')
    .action(
      exitOnError((action: string) => {
        switch (action) {
          case 'enable':
            storeSaveKey('telemetry' as any, 1 as any);
            logger.log('Telemetry turned on');
            break;
          case 'disable':
            storeSaveKey('telemetry' as any, 0 as any);
            logger.log('Telemetry turned off');
            break;
          default:
            throw new DomainError(
              `Unknown telemetry option: Use 'enable' or 'disable' commands`,
            );
        }
      }),
    );
};
