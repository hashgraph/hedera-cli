import stateController from '../state/stateController';
import { Logger } from './logger';

interface CommandAction {
  action: string;
}

interface CommandActions {
  [key: string]: {
    [key: string]: CommandAction;
  };
}

interface CommandOutputs {
  [key: string]: CommandOutput;
}

interface CommandOutput {
  [key: string]: string;
}

const logger = Logger.getInstance();

/**
 * Replace options with arguments or state variables
 *
 * @param options Any Commander Options object
 * @returns
 */
function replaceOptions<T extends Record<string, any>>(options: T): T {
  const state = stateController.getAll();
  if (state.scriptExecution === 0) return options;

  Object.keys(options).forEach((option) => {
    if (option === 'args') return;
    if (typeof options[option] !== 'string') return;

    const regex = /\{\{(.+?)\}\}/g;
    const match = regex.exec(options[option]);
    if (match === null) return;

    const argument = match[1];
    if (!state.scripts[`script-${state.scriptExecutionName}`].args[argument]) {
      logger.error(
        `Unable to find argument value for: ${argument} for script: ${state.scriptExecutionName}`,
      );
      process.exit(1);
    }
    const argumentValue =
      state.scripts[`script-${state.scriptExecutionName}`].args[argument];
    (options as Record<string, any>)[option] = argumentValue;
  });

  return options;
}

const commandActions: CommandActions = {
  // network
  // script
  // backup
  // record
  // setup
  account: {
    // balance
    // clear
    // delete
    // list
    create: {
      action: 'accountCreate',
    },
    import: {
      action: 'accountImport',
    },
    view: {
      action: 'accountView',
    },
  },
  token: {
    // associate
    // transfer
    create: {
      action: 'tokenCreate',
    },
    createFromFile: {
      action: 'tokenCreateFromFile',
    },
  },
  topic: {
    create: {
      action: 'topicCreate',
    },
    messageSubmit: {
      action: 'topicMessageSubmit',
    },
  },
};

const accountOutput: Record<string, string> = {
  alias: 'alias',
  accountId: 'accountId',
  type: 'type',
  publicKey: 'publicKey',
  evmAddress: 'evmAddress',
  solidityAddress: 'solidityAddress',
  solidityAddressFull: 'solidityAddressFull',
  privateKey: 'privateKey',
};

const commandOutputs: CommandOutputs = {
  accountCreate: accountOutput,
  accountImport: accountOutput,
  accountView: {
    accountId: 'accountId',
    balance: 'balance',
    evmAddress: 'evmAddress',
    type: 'type',
    maxAutomaticTokenAssociations: 'maxAutomaticTokenAssociations',
  },
  tokenCreate: {
    tokenId: 'tokenId',
    name: 'name',
    symbol: 'symbol',
    treasuryId: 'treasuryId',
    adminKey: 'adminKey',
  },
  tokenCreateFromFile: {
    tokenId: 'tokenId',
    name: 'name',
    symbol: 'symbol',
    treasuryId: 'treasuryId',
    adminKey: 'adminKey',
    pauseKey: 'pauseKey',
    kycKey: 'kycKey',
    wipeKey: 'wipeKey',
    freezeKey: 'freezeKey',
    supplyKey: 'supplyKey',
    feeScheduleKey: 'feeScheduleKey',
    treasuryKey: 'treasuryKey',
  },
  topicCreate: {
    adminKey: 'adminKey',
    submitKey: 'submitKey',
    topicId: 'topicId',
  },
  topicMessageSubmit: {
    sequenceNumber: 'sequenceNumber',
  },
};

function storeArgs(
  args: string[],
  action: string,
  output: Record<string, any>,
) {
  const state = stateController.getAll();
  if (state.scriptExecution === 0) return;

  // return if action doesn't have output
  if (action === '' || !action) return;

  let stateArgs: Record<string, string> = {};

  args.forEach((arg) => {
    const splittedArg = arg.split('-->');
    const commandOutputName = splittedArg[0];
    const variableName = splittedArg[1];
    const outputVar = commandOutputs[action][commandOutputName];
    stateArgs[variableName] = output[outputVar];
  });

  const newScripts = { ...state.scripts };
  const newArgs = {
    ...newScripts[`script-${state.scriptExecutionName}`].args,
    ...stateArgs,
  };
  newScripts[`script-${state.scriptExecutionName}`].args = newArgs;
  stateController.saveKey('scripts', newScripts);
}

const dynamicVariables = {
  storeArgs,
  replaceOptions,
  commandActions,
};

export default dynamicVariables;
