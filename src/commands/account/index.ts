import { Command } from 'commander';
import deleteCommand from './delete';
import balanceCommand from './balance';
import createCommand from './create';
import importCommand from './import';
import listCommand from './list';
import clearCommand from './clear';
import viewCommand from './view';

export default (program: Command) => {
  const account = program.command('account');

  deleteCommand(account);
  balanceCommand(account);
  createCommand(account);
  importCommand(account);
  listCommand(account);
  clearCommand(account);
  viewCommand(account);
};
