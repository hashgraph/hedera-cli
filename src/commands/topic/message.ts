import stateUtils from '../../utils/state';
import telemetryUtils from '../../utils/telemetry';
import { Logger } from '../../utils/logger';
import stateController from '../../state/stateController';
import dynamicVariablesUtils from '../../utils/dynamicVariables';
import { TopicMessageSubmitTransaction, PrivateKey } from '@hashgraph/sdk';
import api from '../../api';

import type { Command, Filter } from '../../../types';

const logger = Logger.getInstance();

interface SubmitMessageOptions {
  message: string;
  topicId: string;
  args: string[];
}

interface FindMessageOptions {
  sequenceNumber: string;
  sequenceNumberGt: string;
  sequenceNumberGte: string;
  sequenceNumberLt: string;
  sequenceNumberLte: string;
  sequenceNumberEq: string;
  sequenceNumberNe: string;
  topicId: string;
}

/**
 * Format the filters based on the options provided.
 * @param filters The filters to populate.
 * @param options The options provided.
 */
function formatFilters(filters: Filter[], options: FindMessageOptions) {
  if (options.sequenceNumberGt) {
    filters.push({
      field: 'sequencenumber',
      operation: 'gt',
      value: Number(options.sequenceNumberGt),
    });
  }
  if (options.sequenceNumberLt) {
    filters.push({
      field: 'sequencenumber',
      operation: 'lt',
      value: Number(options.sequenceNumberLt),
    });
  }
  if (options.sequenceNumberGte) {
    filters.push({
      field: 'sequencenumber',
      operation: 'gte',
      value: Number(options.sequenceNumberGte),
    });
  }
  if (options.sequenceNumberLte) {
    filters.push({
      field: 'sequencenumber',
      operation: 'lte',
      value: Number(options.sequenceNumberLte),
    });
  }
  if (options.sequenceNumberEq) {
    filters.push({
      field: 'sequencenumber',
      operation: 'eq',
      value: Number(options.sequenceNumberEq),
    });
  }
  if (options.sequenceNumberNe) {
    filters.push({
      field: 'sequencenumber',
      operation: 'ne',
      value: Number(options.sequenceNumberNe),
    });
  }
}

export default (program: any) => {
  const message = program.command('message');

  message
    .command('submit')
    .hook('preAction', async (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
    })
    .description('Submit a message to a topic')
    .requiredOption('-m, --message <message>', 'Submit a message to the topic')
    .requiredOption('-t, --topic-id <topicId>', 'The topic ID')
    .option(
      '--args <args>',
      'Store arguments for scripts',
      (value: string, previous: string) =>
        previous ? previous.concat(value) : [value],
      [],
    )
    .action(async (options: SubmitMessageOptions) => {
      options = dynamicVariablesUtils.replaceOptions(options); // allow dynamic vars for topic-id
      logger.verbose(`Submitting message to topic: ${options.topicId}`);

      const client = stateUtils.getHederaClient();

      let sequenceNumber;
      try {
        const submitMessageTx = await new TopicMessageSubmitTransaction({
          topicId: options.topicId,
          message: options.message,
        }).freezeWith(client);

        // Signing if submit key is set (if it exists in the state - otherwise skip this step)
        const topics = stateController.get('topics');
        if (topics[options.topicId] && topics[options.topicId].submitKey) {
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
      dynamicVariablesUtils.storeArgs(
        options.args,
        dynamicVariablesUtils.commandActions.topic.messageSubmit.action,
        { sequenceNumber: sequenceNumber.toString() },
      );
    });

  message
    .command('find')
    .hook('preAction', async (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
    })
    .description('Find a message by sequence number')
    .option('-s, --sequence-number <sequenceNumber>', 'The sequence number')
    .option('-t, --topic-id <topicId>', 'The topic ID')
    .option(
      '--sequence-number-gt <sequenceNumberGt>',
      'The sequence number greater than',
    )
    .option(
      '--sequence-number-lt <sequenceNumberLt>',
      'The sequence number less than',
    )
    .option(
      '--sequence-number-gte <sequenceNumberGte>',
      'The sequence number greater than or equal to',
    )
    .option(
      '--sequence-number-lte <sequenceNumberLte>',
      'The sequence number less than or equal to',
    )
    .option(
      '--sequence-number-eq <sequenceNumberEq>',
      'The sequence number equal to',
    )
    .option(
      '--sequence-number-ne <sequenceNumberNe>',
      'The sequence number not equal to',
    )
    .action(async (options: FindMessageOptions) => {
      options = dynamicVariablesUtils.replaceOptions(options); // allow dynamic vars for topic-id and sequence-number
      logger.verbose(`Finding message for topic: ${options.topicId}`);

      // Define the keys of options we are interested in
      const sequenceNumberOptions: string[] = [
        'sequenceNumberGt',
        'sequenceNumberLt',
        'sequenceNumberGte',
        'sequenceNumberLte',
        'sequenceNumberEq',
        'sequenceNumberNe',
      ];

      // Check if any of the sequence number options is set
      const isAnyOptionSet = sequenceNumberOptions.some(
        (option: string) => options[option as keyof FindMessageOptions],
      );

      if (!isAnyOptionSet && !options.sequenceNumber) {
        logger.error(
          'Please provide a sequence number or a sequence number filter',
        );
        return;
      }

      if (!isAnyOptionSet) {
        // If no sequence number options are set, proceed with the original logic
        const response = await api.topic.findMessage(
          options.topicId,
          Number(options.sequenceNumber),
        );
        logger.log(
          `Message found: "${Buffer.from(
            response.data.message,
            'base64',
          ).toString('ascii')}"`,
        );
        return;
      }

      // Assuming options can include multiple filters
      let filters: Filter[] = []; // Populate this based on the options provided
      formatFilters(filters, options);

      // Call the new API function
      const response = await api.topic.findMessagesWithFilters(
        options.topicId,
        filters,
      );

      if (response.data.messages.length === 0) {
        logger.log('No messages found');
        return;
      }

      response.data.messages.forEach((el) => {
        logger.log(
          `Message ${el.sequence_number}: "${Buffer.from(
            el.message,
            'base64',
          ).toString('ascii')}"`,
        );
      });
    });
};
