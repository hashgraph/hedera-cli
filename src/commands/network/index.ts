import useCommand from './use';
import listCommand from './list';

import type { Command } from '../../../types';

export default (program: any) => {
  const network = program.command('network').description('Handle networks');

  useCommand(network);
  listCommand(network);

  network.command('*').action((options: any, command: Command) => {
    console.error(`Unknown command: network ${command.parent.args[0]}\n`);
    network.outputHelp();
  });
};
