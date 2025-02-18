import stateUtils from '../../utils/state';
import { Logger } from '../../utils/logger';
import { myParseInt } from '../../utils/verification';

import accountUtils from '../../utils/account';
import telemetryUtils from '../../utils/telemetry';
import dynamicVariablesUtils from '../../utils/dynamicVariables';

import type { Command } from '../../../types';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('create')
    .hook('preAction', async (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
    })
    .description(
      'Create a new Hedera account using NEW recovery words and keypair. This is default.',
    )
    .requiredOption('-a, --alias <alias>', 'account must have an alias')
    .option(
      '-b, --balance <balance>',
      'Initial balance in tinybars',
      myParseInt,
      10000,
    )
    .option(
      '-t, --type <type>',
      'Type of account to create (ECDSA or ED25519)',
      'ED25519',
    )
    .option(
      '--auto-associations <autoAssociations>',
      'Set number of automatic associations',
      0,
    )
    .option(
      '--args <args>',
      'Store arguments for scripts',
      (value: string, previous: string) =>
        previous ? previous.concat(value) : [value],
      [],
    )
    .action(async (options: CreateAccountOptions) => {
      logger.verbose(`Creating account with alias: ${options.alias}`);

      options = dynamicVariablesUtils.replaceOptions(options);

      let accountDetails = await accountUtils.createAccount(
        options.balance,
        options.type,
        options.alias,
        Number(options.autoAssociations),
      );

      dynamicVariablesUtils.storeArgs(
        options.args,
        dynamicVariablesUtils.commandActions.account.create.action,
        accountDetails,
      );
    });
};

interface CreateAccountOptions {
  alias: string;
  balance: number;
  type: 'ECDSA' | 'ED25519';
  autoAssociations: number;
  args: string[];
}
