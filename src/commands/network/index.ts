import useCommand from './use';
import listCommand from './list';

export default (program: any) => {
  const network = program.command('network').description('Handle networks');

  useCommand(network);
  listCommand(network);
};
