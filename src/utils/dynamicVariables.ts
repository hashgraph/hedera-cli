import { getState, saveKey as storeSaveKey } from '../state/store';
import { DomainError } from './errors';

interface CommandAction {
  action: string;
}

type CommandActions = Record<string, Record<string, CommandAction>>;

interface CommandOutputs {
  [key: string]: CommandOutput;
}
interface CommandOutput {
  [key: string]: string;
}

/**
 * Replace options with arguments or state variables
 *
 * @param options Any Commander Options object
 * @returns
 */
function replaceOptions<T extends Record<string, any>>(options: T): T {
  const state: any = getState();
  if (!state.scriptExecution?.active) return options;

  Object.keys(options).forEach((option) => {
    if (option === 'args') return;
    if (typeof options[option] !== 'string') return;

    const regex = /\{\{(.+?)\}\}/g;
    const match = regex.exec(options[option]);
    if (match === null) return;

    const argument = match[1];
    const scriptKey = `script-${state.scriptExecution.name}`;
    const scriptEntry = state.scripts[scriptKey];
    if (!scriptEntry) {
      throw new DomainError(
        `Active script ${state.scriptExecution.name} not found in state`,
      );
    }
    if (!scriptEntry.args[argument]) {
      throw new DomainError(
        `Unable to find argument value for: ${argument} for script: ${state.scriptExecution.name}`,
      );
    }
    const argumentValue = scriptEntry.args[argument];
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
  name: 'name',
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
  const state: any = getState();
  if (!state.scriptExecution?.active) return;

  // return if action doesn't have output
  if (action === '' || !action) return;

  let stateArgs: Record<string, string> = {};

  args.forEach((arg) => {
    const splittedArg = arg.split(':');
    const commandOutputName = splittedArg[0];
    const variableName = splittedArg[1];
    const outputVar = commandOutputs[action][commandOutputName];
    stateArgs[variableName] = output[outputVar];
  });

  const scriptKey = `script-${state.scriptExecution.name}`;
  const currentScript = state.scripts[scriptKey];
  if (!currentScript) return; // silently ignore if script missing
  const newScripts = { ...state.scripts } as any;
  const mergedArgs = { ...(currentScript.args || {}), ...stateArgs };
  newScripts[scriptKey] = { ...currentScript, args: mergedArgs };
  storeSaveKey('scripts', newScripts as any);
}

const dynamicVariables = {
  storeArgs,
  replaceOptions,
  commandActions,
};

export default dynamicVariables;
