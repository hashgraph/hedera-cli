import { PrivateKey, TopicCreateTransaction } from '@hashgraph/sdk';
import { Command } from 'commander';
import type { Topic } from '../../../types';
import { addTopic } from '../../state/mutations';
import { heading, success } from '../../utils/color';
import dynamicVariablesUtils from '../../utils/dynamicVariables';
import { DomainError } from '../../utils/errors';
import { Logger } from '../../utils/logger';
import { isJsonOutput, printOutput } from '../../utils/output';
import signUtils from '../../utils/sign';
import stateUtils from '../../utils/state';
import { telemetryPreAction } from '../shared/telemetryHook';
import { wrapAction } from '../shared/wrapAction';

const logger = Logger.getInstance();

export default (program: Command) => {
  program
    .command('create')
    .hook('preAction', telemetryPreAction)
    .description('Create a new topic')
    .addHelpText(
      'afterAll',
      '\nExamples:\n  $ hedera topic create --memo "Announcements"\n  $ hedera topic create --admin-key <key> --submit-key <key> --json',
    )
    .option('-a, --admin-key <adminKey>', 'The admin key')
    .option('-s, --submit-key <submitKey>', 'The submit key')
    .option('--memo <memo>', 'The memo')
    .option(
      '--args <args>',
      'Store arguments for scripts',
      (value: string, previous: string[]) =>
        previous ? previous.concat(value) : [value],
      [] as string[],
    )
    .action(
      wrapAction<CreateTopicOptions>(
        async (options) => {
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

          if (isJsonOutput()) {
            printOutput('topicCreate', { topicId: topicId.toString() });
          } else {
            logger.log(heading('Topic created'));
            logger.log(success(`ID: ${topicId.toString()}`));
          }

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
          // Store script args with topic context
          dynamicVariablesUtils.storeArgs(
            options.args,
            dynamicVariablesUtils.commandActions.topic.create.action,
            topic,
          );
        },
        { log: 'Creating topic' },
      ),
    );
};

interface CreateTopicOptions {
  adminKey: string;
  submitKey: string;
  memo: string;
  args: string[];
}
