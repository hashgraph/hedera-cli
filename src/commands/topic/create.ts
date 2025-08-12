import stateUtils from '../../utils/state';
import { Logger } from '../../utils/logger';
import { DomainError, exitOnError } from '../../utils/errors';
import signUtils from '../../utils/sign';
import telemetryUtils from '../../utils/telemetry';
import { addTopic } from '../../state/mutations';
import dynamicVariablesUtils from '../../utils/dynamicVariables';
import { TopicCreateTransaction, PrivateKey } from '@hashgraph/sdk';

import type { Command, Topic } from '../../../types';

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
    .description('Create a new topic')
    .option('-a, --admin-key <adminKey>', 'The admin key')
    .option('-s, --submit-key <submitKey>', 'The submit key')
    .option('--memo <memo>', 'The memo')
    .option(
      '--args <args>',
      'Store arguments for scripts',
      (value: string, previous: string) =>
        previous ? previous.concat(value) : [value],
      [],
    )
    .action(
      exitOnError(async (options: CreateTopicOptions) => {
        options = dynamicVariablesUtils.replaceOptions(options); // allow dynamic vars for admin-key and submit-key
        logger.verbose('Creating topic');

        const client = stateUtils.getHederaClient();

        let topicId;
        try {
          const topicCreateTx = new TopicCreateTransaction();
          if (options.memo) {
            topicCreateTx.setTopicMemo(options.memo);
          }
          if (options.adminKey) {
            topicCreateTx.setAdminKey(
              PrivateKey.fromStringDer(options.adminKey),
            );
          }
          if (options.submitKey) {
            topicCreateTx.setSubmitKey(
              PrivateKey.fromStringDer(options.submitKey),
            );
          }

          // Signing
          topicCreateTx.freezeWith(client);
          const signedTopicCreateTx = await signUtils.signByType(
            topicCreateTx,
            'topicCreate',
            {
              adminKey: options.adminKey,
              submitKey: options.submitKey,
            },
          );

          const topicCreateTxResponse =
            await signedTopicCreateTx.execute(client);
          const receipt = await topicCreateTxResponse.getReceipt(client);
          topicId = receipt.topicId;
        } catch (error) {
          client.close();
          throw new DomainError('Error creating new topic');
        }

        if (!topicId) {
          client.close();
          throw new DomainError('Failed to create new topic');
        }

        logger.log(`Created new topic: ${topicId.toString()}`);

        const topic: Topic = {
          network: stateUtils.getNetwork(),
          topicId: topicId.toString(),
          adminKey: options.adminKey || '',
          submitKey: options.submitKey || '',
          memo: options.memo || '',
        };

        addTopic(topic, false);
        logger.verbose(`Saved topic to state: ${topicId.toString()}`);

        client.close();
        dynamicVariablesUtils.storeArgs(
          options.args,
          dynamicVariablesUtils.commandActions.topic.create.action,
          topic,
        );
      }),
    );
};

interface CreateTopicOptions {
  adminKey: string;
  submitKey: string;
  memo: string;
  args: string[];
}
