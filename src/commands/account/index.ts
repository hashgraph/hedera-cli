import { Command } from 'commander';
import balanceCommand from './balance';
import clearCommand from './clear';
import createCommand from './create';
import deleteCommand from './delete';
import importCommand from './import';
import listCommand from './list';
import viewCommand from './view';

export default (program: Command) => {
  const account = program.command('account').alias('acct');

  deleteCommand(account);
  balanceCommand(account);
  createCommand(account);
  importCommand(account);
  listCommand(account);
  clearCommand(account);
  viewCommand(account);
};
