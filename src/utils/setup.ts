import { AccountId } from '@hashgraph/sdk';
import { updateState as storeUpdateState } from '../state/store';
import accountUtils from './account';
import type { Account } from '../../types';

// rewrite the function below to create an a single operator account for each network with the network name as a prefix
function setupOperatorAccount(
  operatorId: string,
  operatorKey: string,
  network: string,
): void {
  if (!operatorId || !operatorKey) return;
  const privateKeyObject = accountUtils.getPrivateKeyObject(operatorKey);
  const solidity = AccountId.fromString(operatorId).toSolidityAddress();
  const acct: Account = {
    accountId: operatorId,
    privateKey: operatorKey,
    network,
    name: `${network}-operator`,
    type: 'ECDSA',
    publicKey: privateKeyObject.publicKey.toStringDer(),
    evmAddress: privateKeyObject.publicKey.toEvmAddress(),
    solidityAddress: solidity,
    solidityAddressFull: `0x${solidity}`,
  };
  storeUpdateState((draft) => {
    draft.accounts[acct.name] = acct;
  });
}

const setupUtils = {
  setupOperatorAccount,
};

export default setupUtils;
