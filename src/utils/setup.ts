import { AccountId, PrivateKey } from '@hashgraph/sdk';
import stateController from '../state/stateController';
import accountUtils from './account';
import { Account, State } from '../../types';

// rewrite the function below to create an a single operator account for each network with the network name as a prefix
function setupOperatorAccount(
  operatorId: string,
  operatorKey: string,
  network: string,
): void {
  const state = stateController.getAll();
  let newState: State = { ...state };
  if (!newState.accounts) {
    newState.accounts = {};
  }
  if (!operatorId || !operatorKey) {
    return; // nothing to do
  }
  const privateKeyObject = accountUtils.getPrivateKeyObject(operatorKey);

  newState.accounts[`${network}-operator`] = {
    accountId: operatorId,
    privateKey: operatorKey,
    network: network,
    name: `${network}-operator`,
    type: 'ECDSA',
    publicKey: privateKeyObject.publicKey.toStringDer(),
    evmAddress: privateKeyObject.publicKey.toEvmAddress(),
    // FIXME - why are these here?
    // hexKey: `0x${PrivateKey.fromStringDer(operatorKey).toStringRaw()}`,
    // evmKey: `0x${privateKeyObject.toStringRaw()}`,
    solidityAddress: `${AccountId.fromString(operatorId).toSolidityAddress()}`,
    solidityAddressFull: `0x${AccountId.fromString(operatorId).toSolidityAddress()}`,
  } as Account;
  stateController.saveState(newState);
}

const setupUtils = {
  setupOperatorAccount,
};

export default setupUtils;
