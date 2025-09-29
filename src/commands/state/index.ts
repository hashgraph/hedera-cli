import { Command } from 'commander';
import clearCommand from './clear';
import downloadCommand from './download';
import viewCommand from './view';

export default (program: Command) => {
  const state = program
    .command('state')
    .alias('st')
    .description('Manage CLI state');
  clearCommand(state);
  downloadCommand(state);
  viewCommand(state);
};
