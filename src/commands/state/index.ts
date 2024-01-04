import clearCommand from './clear';
import downloadCommand from './download';
import viewCommand from './view';

export default (program: any) => {
  const state = program.command('state').description('Manage CLI state');

  clearCommand(state);
  downloadCommand(state);
  viewCommand(state);
};
