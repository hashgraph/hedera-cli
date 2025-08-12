import { AccountId } from '@hashgraph/sdk';
import { updateState as storeUpdateState } from '../state/store';
import accountUtils from './account';
import { Account, State } from '../../types';

// rewrite the function below to create an a single operator account for each network with the network name as a prefix
function setupOperatorAccount(
  operatorId: string,
  operatorKey: string,
  network: string,
): void {
  // state retrieval removed (unused)
  // We'll do mutation via updateState to avoid frozen object issues during migration.
  if (!operatorId || !operatorKey) {
    return; // nothing to do
  }
  const privateKeyObject = accountUtils.getPrivateKeyObject(operatorKey);
  storeUpdateState((draft: State) => {
    if (!draft.accounts) draft.accounts = {} as any;
    draft.accounts[`${network}-operator`] = {
      accountId: operatorId,
      privateKey: operatorKey,
      network: network,
      name: `${network}-operator`,
      type: 'ECDSA',
      publicKey: privateKeyObject.publicKey.toStringDer(),
      evmAddress: privateKeyObject.publicKey.toEvmAddress(),
      solidityAddress: `${AccountId.fromString(operatorId).toSolidityAddress()}`,
      solidityAddressFull: `0x${AccountId.fromString(operatorId).toSolidityAddress()}`,
    } as Account;
  });
}

const setupUtils = {
  setupOperatorAccount,
};

export default setupUtils;
