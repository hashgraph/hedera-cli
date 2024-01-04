import * as fs from 'fs';
import * as path from 'path';
import { prompt } from 'enquirer';

import { recordCommand } from '../state/stateService';
import stateController from '../state/stateController';
import { Logger } from '../utils/logger';

import type { Command, State } from '../../types';

const logger = Logger.getInstance();

export default (program: any) => {
  const network = program.command('backup');

  network
    .command('create')
    .hook('preAction', (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description('Create a backup of the config.json file')
    .option('--accounts', 'Backup the accounts')
    .option('--safe', 'Remove the private keys from the backup')
    .action((options: HbarTransferOptions) => {
      
    });

  
};

interface HbarTransferOptions {
  accounts: boolean;
  safe: boolean;
}
