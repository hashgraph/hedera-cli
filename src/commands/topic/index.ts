import createCommand from './create';
import messageCommand from './message';
import listCommand from './list';

export default (program: any) => {
  const state = program
    .command('topic')
    .description(
      'Hedera Consensus Service commands handling topics and messages',
    );

  createCommand(state);
  messageCommand(state);
  listCommand(state);
};
