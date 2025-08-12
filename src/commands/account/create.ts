import stateUtils from '../../utils/state';
import { Logger } from '../../utils/logger';
import { myParseInt } from '../../utils/verification';

import accountUtils from '../../utils/account';
import { exitOnError } from '../../utils/errors';
import telemetryUtils from '../../utils/telemetry';
import dynamicVariablesUtils from '../../utils/dynamicVariables';

import { Command } from 'commander';

const logger = Logger.getInstance();

export default (program: Command) => {
  program
    .command('create')
    .hook('preAction', async (thisCommand: Command) => {
      const parentName = thisCommand.parent?.name() || 'unknown';
      const command = [parentName, ...(thisCommand.parent?.args ?? [])];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
    })
    .description(
      'Create a new Hedera account using NEW recovery words and keypair. This is default.',
    )
    .requiredOption(
      '-n, --name <name>',
      'account must have a name for internal referencing in the CLI state',
    )
    .option(
      '-b, --balance <balance>',
      'Initial balance in tinybars',
      myParseInt,
      10000,
    )
    .option(
      '--auto-associations <autoAssociations>',
      'Set number of automatic associations',
      (value: string) => Number(value),
      0,
    )
    .option(
      '--args <arg>',
      'Store arguments for scripts (repeatable)',
      (value: string, previous: string[]) =>
        previous ? [...previous, value] : [value],
      [] as string[],
    )
    .action(
      exitOnError(async (options: CreateAccountOptions) => {
        logger.verbose(`Creating account with name: ${options.name}`);

        options = dynamicVariablesUtils.replaceOptions(options);
        const accountDetails = await accountUtils.createAccount(
          options.balance,
          'ECDSA',
          options.name,
          Number(options.autoAssociations),
        );

        dynamicVariablesUtils.storeArgs(
          options.args,
          dynamicVariablesUtils.commandActions.account.create.action,
          accountDetails,
        );
      }),
    );
};

interface CreateAccountOptions {
  name: string;
  balance: number;
  type: 'ECDSA';
  autoAssociations: number;
  args: string[];
}
