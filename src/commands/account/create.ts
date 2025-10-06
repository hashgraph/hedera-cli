import accountUtils from '../../utils/account';
import { isJsonOutput, printOutput } from '../../utils/output';
import { myParseInt, parseIntOption } from '../../utils/verification';
// Removed direct exitOnError usage; wrapAction handles error wrapping
import dynamicVariablesUtils from '../../utils/dynamicVariables';
import { telemetryPreAction } from '../shared/telemetryHook';
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
          if (isJsonOutput()) {
            printOutput('accountCreate', {
              name: accountDetails.name,
              accountId: accountDetails.accountId,
              type: accountDetails.type,
              publicKey: accountDetails.publicKey,
              evmAddress: accountDetails.evmAddress,
              network: accountDetails.network,
              solidityAddress: accountDetails.solidityAddress,
            });
          }
          dynamicVariablesUtils.storeArgs(
            options.args,
            dynamicVariablesUtils.commandActions.account.create.action,
            accountDetails,
          );
        },
        { log: (o) => `Creating account with name: ${o.name}` },
      ),
    );
  program.addHelpText(
    'afterAll',
    '\nExamples:\n  $ hedera account create -n alice -b 10000\n  $ hedera account create -n bob --json',
  );
};

interface CreateAccountOptions {
  name: string;
  balance: number;
  type: 'ECDSA';
  autoAssociations: number;
  args: string[];
}
