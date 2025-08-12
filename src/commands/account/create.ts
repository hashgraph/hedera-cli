import { myParseInt, parseIntOption } from '../../utils/verification';
import accountUtils from '../../utils/account';
// Removed direct exitOnError usage; wrapAction handles error wrapping
import { telemetryPreAction } from '../shared/telemetryHook';
import dynamicVariablesUtils from '../../utils/dynamicVariables';
import { wrapAction } from '../shared/wrapAction';

import { Command } from 'commander';

// logger handled via wrapAction config

export default (program: Command) => {
  program
    .command('create')
    .hook('preAction', telemetryPreAction)
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
      parseIntOption,
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
      wrapAction<CreateAccountOptions>(
        async (options) => {
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
        },
        { log: (o) => `Creating account with name: ${o.name}` },
      ),
    );
};

interface CreateAccountOptions {
  name: string;
  balance: number;
  type: 'ECDSA';
  autoAssociations: number;
  args: string[];
}
