import { Command } from 'commander';
import createCommand from './create';
import messageCommand from './message';
import listCommand from './list';

export default (program: Command) => {
  const topic = program
    .command('topic')
    .alias('tpc')
    .description(
      'Hedera Consensus Service commands handling topics and messages',
    );

  createCommand(topic);
  messageCommand(topic);
  listCommand(topic);
};
