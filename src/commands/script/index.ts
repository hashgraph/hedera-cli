import deleteCommand from './delete';
import listCommand from './list';
import loadCommand from './load';

export default (program: any) => {
  const script = program
    .command('script')
    .description('Handle and execute scripts');

  deleteCommand(script);
  listCommand(script);
  loadCommand(script);
};
