import createCommand from './create';

export default (program: any) => {
  const state = program.command('topic').description('Hedera Consensus Service commands handling topics and messages');

  createCommand(state);
};
