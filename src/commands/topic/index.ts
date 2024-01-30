import createCommand from './create';
import submitCommand from './submit';
import listCommand from './list';

export default (program: any) => {
  const state = program
    .command('topic')
    .description(
      'Hedera Consensus Service commands handling topics and messages',
    );

  createCommand(state);
  submitCommand(state);
  listCommand(state);
};
