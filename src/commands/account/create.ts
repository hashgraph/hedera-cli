import { recordCommand } from '../../state/stateService';
import { Logger } from '../../utils/logger';
import { myParseInt } from '../../utils/verification';
import stateController from '../../state/stateController';

import accountUtils from '../../utils/account';

import type { Command, Account, Token } from '../../../types';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('create')
    .hook('preAction', (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description(
      'Create a new Hedera account using NEW recovery words and keypair. This is default.',
    )
    .requiredOption('-a, --alias <alias>', 'account must have an alias')
    .option(
      '-b, --balance <balance>',
      'Initial balance in tinybars',
      myParseInt,
      1000,
    )
    .option(
      '-t, --type <type>',
      'Type of account to create (ECDSA or ED25519)',
      'ED25519',
    )
    .option(
      '--args <args>',
      'Store arguments for scripts',
      (value: string, previous: string) =>
        previous ? previous.concat(value) : [value],
      [],
    )
    .action(async (options: CreateAccountOptions) => {
      try {
        options = replaceOptionsWithArgs(options);
        console.log(options);
        /*let accountDetails = await accountUtils.createAccount(
          options.balance,
          options.type,
          options.alias,
        );*/
        /*const accountDetails = {
          alias: 'alice',
          accountId: '0.0.6025067',
          type: 'ED25519',
          publicKey:
            '302a300506032b6570032100052ff6e06c1610e33c1c631fa44c259ab62c7becb7a97932b3d60094d0a2f8ba',
          evmAddress: '',
          solidityAddress: '00000000000000000000000000000000005bef6b',
          solidityAddressFull: '0x00000000000000000000000000000000005bef6b',
          privateKey:
            '302e020100300506032b657004220420ece0b15b20e555f66d5f4cd83187567af9613276629d7e15161b0c929ea07697',
        };

        storeArgs(
          options.args,
          commandActions.account.create.action,
          accountDetails,
        );*/
      } catch (error) {
        logger.error(error as object);
      }
    });
};

function replaceOptionsWithArgs<T extends Record<string, any>>(options: T): T {
  const state = stateController.getAll();
  if (state.scriptExecution === 0) return options;

  Object.keys(options).forEach(option => {
    if (option === "args") return;
    if (typeof options[option] !== "string") return;

    const regex = /\{\{(.+?)\}\}/g;
    const match = regex.exec(options[option]);
    if (match === null) return;

    const argument = match[1];
    const argumentValue = state.scripts[`script-${state.scriptExecutionName}`].args[argument];
    (options as Record<string, any>)[option] = argumentValue;
  })

  return options;
}

interface CommandAction {
  action: string;
}

interface CommandActions {
  [key: string]: {
    [key: string]: CommandAction;
  };
}

const commandActions: CommandActions = {
  account: {
    create: {
      action: 'accountCreate',
    },
  },
  token: {
    associate: {
      action: 'tokenAssociate',
    },
  },
};

interface CommandOutputs {
  [key: string]: CommandOutput;
}

interface CommandOutput {
  [key: number]: string;
}

const commandOutputs: CommandOutputs = {
  accountCreate: {
    1: 'alias',
    2: 'accountId',
    3: 'type',
    4: 'publicKey',
    5: 'evmAddress',
    6: 'solidityAddress',
    7: 'solidityAddressFull',
    8: 'privateKey',
  },
};

function storeArgs(
  args: string[],
  action: string,
  output: Record<string, any>,
) {
  const state = stateController.getAll();
  if (state.scriptExecution === 0) return;

  let stateArgs: Record<string, string> = {};

  args.forEach((arg) => {
    const splittedArg = arg.split(',');
    const position = Number(splittedArg[0]);
    const variableName = splittedArg[1];
    const outputVar = commandOutputs[action][position];
    stateArgs[variableName] = output[outputVar];
  });

  const newScripts = {...state.scripts}
  newScripts[`script-${state.scriptExecutionName}`].args = stateArgs;
  stateController.saveKey('scripts', newScripts);
}

interface CreateAccountOptions {
  alias: string;
  balance: number;
  type: 'ECDSA' | 'ED25519';
  args: string[];
}
