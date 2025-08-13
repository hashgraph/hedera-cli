import { Command } from 'commander';
import useCommand from './use';
import listCommand from './list';

export default (program: Command) => {
  const network = program
    .command('network')
    .alias('net')
    .description('Handle networks');

  useCommand(network);
  listCommand(network);
};
