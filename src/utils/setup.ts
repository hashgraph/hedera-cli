import { AccountId } from '@hashgraph/sdk';
import stateController from '../state/stateController';
import accountUtils from './account';

/**
 * @description Setup operator accounts for previewnet, testnet, and mainnet in the state file
 */
function setupOperatorAccounts(
  testnetOperatorId: string,
  testnetOperatorKey: string,
  mainnetOperatorId: string,
  mainnetOperatorKey: string,
  previewnetOperatorId: string,
  previewnetOperatorKey: string,
): void {
  const state = stateController.getAll();
  let newState = { ...state };
  newState.testnetOperatorKey = testnetOperatorKey;
  newState.testnetOperatorId = testnetOperatorId;
  newState.mainnetOperatorKey = mainnetOperatorKey;
  newState.mainnetOperatorId = mainnetOperatorId;
  newState.previewnetOperatorId = previewnetOperatorId;
  newState.previewnetOperatorKey = previewnetOperatorKey;

  if (testnetOperatorId) {
    const privateKeyObject =
      accountUtils.getPrivateKeyObject(testnetOperatorKey);
    const type = accountUtils.getKeyType(testnetOperatorKey);

    newState.accounts['testnet-operator'] = {
      accountId: testnetOperatorId,
      privateKey: testnetOperatorKey,
      network: 'testnet',
      alias: 'testnet-operator',
      type,
      publicKey: privateKeyObject.publicKey.toStringDer(),
      evmAddress:
        type === 'ed25519' ? '' : privateKeyObject.publicKey.toEvmAddress(),
      solidityAddress: `${AccountId.fromString(
        testnetOperatorId,
      ).toSolidityAddress()}`,
      solidityAddressFull: `0x${AccountId.fromString(
        testnetOperatorId,
      ).toSolidityAddress()}`,
    };
  }

  if (previewnetOperatorId) {
    const privateKeyObject = accountUtils.getPrivateKeyObject(
      previewnetOperatorKey,
    );
    const type = accountUtils.getKeyType(previewnetOperatorKey);

    newState.accounts['preview-operator'] = {
      accountId: previewnetOperatorId,
      privateKey: previewnetOperatorKey,
      network: 'previewnet',
      alias: 'preview-operator',
      type,
      publicKey: privateKeyObject.publicKey.toStringDer(),
      evmAddress:
        type === 'ed25519' ? '' : privateKeyObject.publicKey.toEvmAddress(),
      solidityAddress: `${AccountId.fromString(
        previewnetOperatorId,
      ).toSolidityAddress()}`,
      solidityAddressFull: `0x${AccountId.fromString(
        previewnetOperatorId,
      ).toSolidityAddress()}`,
    };
  }

  if (mainnetOperatorId) {
    const privateKeyObject =
      accountUtils.getPrivateKeyObject(mainnetOperatorKey);
    const type = accountUtils.getKeyType(mainnetOperatorKey);

    newState.accounts['mainnet-operator'] = {
      accountId: mainnetOperatorId,
      privateKey: mainnetOperatorKey,
      network: 'mainnet',
      alias: 'mainnet-operator',
      type,
      publicKey: privateKeyObject.publicKey.toStringDer(),
      evmAddress:
        type === 'ed25519' ? '' : privateKeyObject.publicKey.toEvmAddress(),
      solidityAddress: `${AccountId.fromString(
        mainnetOperatorId,
      ).toSolidityAddress()}`,
      solidityAddressFull: `0x${AccountId.fromString(
        mainnetOperatorId,
      ).toSolidityAddress()}`,
    };
  }

  newState.network = 'testnet';

  stateController.saveState(newState);
}

const setupUtils = {
  setupOperatorAccounts,
};

export default setupUtils;
