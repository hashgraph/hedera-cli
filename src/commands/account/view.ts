import stateUtils from '../../utils/state';
import telemetryUtils from '../../utils/telemetry';
import dynamicVariablesUtils from '../../utils/dynamicVariables';
import { Logger } from '../../utils/logger';

import type { Command } from '../../../types';
import api from '../../api';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('view')
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
      'View the detials of an account by accound ID. The account can be in the state or external.',
    )
    // We don't allow a name here as you can use the state command to view accounts by name
    // This command is used to look up external accounts via mirror node
    .requiredOption('-i, --id <id>', 'Account ID')
    .option(
      '--args <args>',
      'Store arguments for scripts',
      (value: string, previous: string) =>
        previous ? previous.concat(value) : [value],
      [],
    )
    .action(async (options: ViewAccountOptions) => {
      options = dynamicVariablesUtils.replaceOptions(options); // allow dynamic var for id
      logger.verbose(`Viewing account ${options.id} details`);

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
            balance: response.data.balance.balance,
            evmAddress: response.data.evm_address,
            type:
              response.data.key._type === 'ECDSA_SECP256K1'
                ? 'ECDSA'
                : 'ED25519',
            maxAutomaticTokenAssociations:
              response.data.max_automatic_token_associations,
          },
        );
      } catch (error) {
        logger.error('Failed to get account info:', error as object);
      }
    });
};

interface ViewAccountOptions {
  id: string;
  args: string[];
}
