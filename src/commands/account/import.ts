import { Command } from 'commander';
import accountUtils from '../../utils/account';
import dynamicVariablesUtils from '../../utils/dynamicVariables';
import { isJsonOutput, printOutput } from '../../utils/output';
import { telemetryPreAction } from '../shared/telemetryHook';
import { wrapAction } from '../shared/wrapAction';

export default (program: Command) => {
  program
    .command('import')
    .hook('preAction', telemetryPreAction)
    .description(
      'Import an existing account using an account ID, name, type, and optional private key.',
    )
    .requiredOption('-n, --name <name>', 'account must have a name')
    .requiredOption('-i, --id <id>', 'Account ID')
    .option('-k, --key <key>', 'Private key')
    .option(
      '--args <args>',
      'Store arguments for scripts',
      (value: string, previous: string[]) =>
        previous ? previous.concat(value) : [value],
      [] as string[],
    )
    .action(
      wrapAction<ImportAccountOptions>(
        (options) => {
          const accountDetails = options.key
            ? accountUtils.importAccount(options.id, options.key, options.name)
            : accountUtils.importAccountId(options.id, options.name);
          if (isJsonOutput()) {
            printOutput('accountImport', {
              name: accountDetails.name,
              accountId: accountDetails.accountId,
              type: accountDetails.type,
              network: accountDetails.network,
            });
          }
          dynamicVariablesUtils.storeArgs(
            options.args,
            dynamicVariablesUtils.commandActions.account.import.action,
            accountDetails,
          );
        },
        { log: (o) => `Importing account with name: ${o.name}` },
      ),
    );
  program.addHelpText(
    'afterAll',
    '\nExamples:\n  $ hedera account import -n alice -i 0.0.1234 -k <privateKey>\n  $ hedera account import -n external -i 0.0.2222 --json',
  );
};

interface ImportAccountOptions {
  name: string;
  id: string;
  key: string;
  args: string[];
}
