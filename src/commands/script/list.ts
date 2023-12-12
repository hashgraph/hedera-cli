const axios = require('axios');

import { recordCommand } from '../../state/stateService';
import scriptUtils from '../../utils/script';
import type { Command, Script } from '../../../types';

export default (program: any) => {
  program
    .command('list')
    .hook('preAction', (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description('List all scripts')
    .action(() => {
      scriptUtils.listScripts();
    });
};
