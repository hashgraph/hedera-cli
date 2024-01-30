import stateUtils from '../../utils/state';
import { Logger } from '../../utils/logger';
import stateController from '../../state/stateController';
import dynamicVariablesUtils from '../../utils/dynamicVariables';
import { TopicMessageSubmitTransaction, PrivateKey } from '@hashgraph/sdk';

import type { Command } from '../../../types';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('submit')
    .hook('preAction', (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      stateUtils.recordCommand(command);
    })
    .description('Create a new topic')
    .option('-m, --message <message>', 'Submit a message to the topic')
    .option('-t, --topic-id <topicId>', 'The topic ID')
    .action(async (options: SubmitMessageOptions) => {
      options = dynamicVariablesUtils.replaceOptions(options); // allow dynamic vars for admin-key and submit-key
      logger.verbose(`Submitting message to topic: ${options.topicId}`);

      const client = stateUtils.getHederaClient();

      let sequenceNumber;
      try {
        const submitMessageTx = await new TopicMessageSubmitTransaction({
            topicId: options.topicId,
            message: options.message,
        })
        .freezeWith(client);

        // Signing if submit key is set
        const topics = stateController.get('topics');
        if (topics[options.topicId].submitKey) {
            const submitKey = PrivateKey.fromStringDer(
                topics[options.topicId].submitKey,
            );
            submitMessageTx.sign(submitKey);
        }

        const topicMessageTxResponse = await submitMessageTx.execute(client);
        const receipt = await topicMessageTxResponse.getReceipt(client);
        sequenceNumber = receipt.topicSequenceNumber;
      } catch (error) {
        logger.error('Error sending message to topic', error as object);
        client.close();
        process.exit(1);
      }

      logger.log(`Message submitted with sequence number: ${sequenceNumber}`);
      client.close();
    });
};

interface SubmitMessageOptions {
    message: string;
    topicId: string;
}
