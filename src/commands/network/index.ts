import { Command } from 'commander';
import addCommand from './add';
import listCommand from './list';
import useCommand from './use';

export default (program: Command) => {
  const network = program
    .command('network')
    .alias('net')
    .description('Handle networks');

  useCommand(network);
  listCommand(network);
  addCommand(network);
};
