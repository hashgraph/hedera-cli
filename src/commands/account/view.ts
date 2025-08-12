import { telemetryPreAction } from '../shared/telemetryHook';
import dynamicVariablesUtils from '../../utils/dynamicVariables';
import { Logger } from '../../utils/logger';
import { Command } from 'commander';
import { wrapAction } from '../shared/wrapAction';
import api from '../../api';

const logger = Logger.getInstance();

export default (program: Command) => {
  program
    .command('view')
    .hook('preAction', telemetryPreAction)
    .description(
      'View the detials of an account by accound ID. The account can be in the state or external.',
    )
    // We don't allow a name here as you can use the state command to view accounts by name
    // This command is used to look up external accounts via mirror node
    .requiredOption('-i, --id <id>', 'Account ID')
    .option(
      '--args <args>',
      'Store arguments for scripts',
      (value: string, previous: string[]) =>
        previous ? previous.concat(value) : [value],
      [] as string[],
    )
    .action(
      wrapAction<ViewAccountOptions>(
        async (options) => {
          // Attempt mirror node lookup for external visibility
          try {
            const response = await api.account.getAccountInfo(options.id);
            logger.log(`Account: ${response.data.account}`);
            logger.log(`Balance Tinybars: ${response.data.balance.balance}`);
            logger.log(`Deleted: ${response.data.deleted}`);
            logger.log(`EVM Address: ${response.data.evm_address}`);
            logger.log(
              `Key type: ${response.data.key._type} - Key: ${response.data.key.key}`,
            );
            logger.log(
              `Max automatic token associations: ${response.data.max_automatic_token_associations}`,
            );
            dynamicVariablesUtils.storeArgs(
              options.args,
              dynamicVariablesUtils.commandActions.account.view.action,
              {
                accountId: response.data.account,
                balance: response.data.balance.balance.toString(),
                evmAddress: response.data.evm_address,
                type:
                  response.data.key._type === 'ECDSA_SECP256K1'
                    ? 'ECDSA'
                    : 'ED25519',
                maxAutomaticTokenAssociations:
                  response.data.max_automatic_token_associations.toString(),
              },
            );
          } catch (err) {
            logger.error('Failed to get account info:', err as object);
          }
        },
        { log: (o) => `Viewing account ${o.id} details` },
      ),
    );
};

interface ViewAccountOptions {
  id: string;
  args: string[];
}
