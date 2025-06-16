import compileCommand from './compile';
import deployCommand from './deploy';

export default (program: any) => {
  const state = program
    .command('smartcontract')
    .description(
      'Hedera Smart Contract commands. Compile, deploy, call, and view smart contracts.',
    );

  compileCommand(state);
  deployCommand(state);
};
