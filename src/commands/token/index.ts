import associateCommand from './associate';
import createCommand from './create';
import createFromFileCommand from './createFromFile';
import transferCommand from './transfer';

export default (program: any) => {
  const token = program.command('token').description('Handle tokens');

  associateCommand(token);
  createCommand(token);
  createFromFileCommand(token);
  transferCommand(token);
};
