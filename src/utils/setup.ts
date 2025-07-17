import { AccountId, PrivateKey } from '@hashgraph/sdk';
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
  localnetOperatorId: string,
  localnetOperatorKey: string,
): void {
  const state = stateController.getAll();
  let newState = { ...state };
  newState.testnetOperatorKey = testnetOperatorKey;
  newState.testnetOperatorId = testnetOperatorId;
  newState.mainnetOperatorKey = mainnetOperatorKey;
  newState.mainnetOperatorId = mainnetOperatorId;
  newState.previewnetOperatorId = previewnetOperatorId;
  newState.previewnetOperatorKey = previewnetOperatorKey;
  newState.localnetOperatorKey = localnetOperatorKey;
  newState.localnetOperatorId = localnetOperatorId;

  if (testnetOperatorId) {
    const privateTestnetKeyEcdsa = PrivateKey.fromStringDer(testnetOperatorKey);
    newState.testnetOperatorKeyHex = `0x${privateTestnetKeyEcdsa.toStringRaw()}`;

    const privateKeyObject =
      accountUtils.getPrivateKeyObject(testnetOperatorKey);

    newState.accounts['testnet-operator'] = {
      accountId: testnetOperatorId,
      privateKey: testnetOperatorKey,
      network: 'testnet',
      alias: 'testnet-operator',
      type: 'ECDSA',
      publicKey: privateKeyObject.publicKey.toStringDer(),
      evmAddress: privateKeyObject.publicKey.toEvmAddress(),
      solidityAddress: `${AccountId.fromString(
        testnetOperatorId,
      ).toSolidityAddress()}`,
      solidityAddressFull: `0x${AccountId.fromString(
        testnetOperatorId,
      ).toSolidityAddress()}`,
    };
  }

  if (previewnetOperatorId) {
    const privatePreviewnetKeyEcdsa = PrivateKey.fromStringDer(
      previewnetOperatorKey,
    );
    newState.previewnetOperatorKeyHex = `0x${privatePreviewnetKeyEcdsa.toStringRaw()}`;

    const privateKeyObject = accountUtils.getPrivateKeyObject(
      previewnetOperatorKey,
    );

    newState.accounts['preview-operator'] = {
      accountId: previewnetOperatorId,
      privateKey: previewnetOperatorKey,
      network: 'previewnet',
      alias: 'preview-operator',
      type: 'ECDSA',
      publicKey: privateKeyObject.publicKey.toStringDer(),
      evmAddress: privateKeyObject.publicKey.toEvmAddress(),
      solidityAddress: `${AccountId.fromString(
        previewnetOperatorId,
      ).toSolidityAddress()}`,
      solidityAddressFull: `0x${AccountId.fromString(
        previewnetOperatorId,
      ).toSolidityAddress()}`,
    };
  }

  if (mainnetOperatorId) {
    const privateMainnetKeyEcdsa = PrivateKey.fromStringDer(mainnetOperatorKey);
    newState.mainnetOperatorKeyHex = `0x${privateMainnetKeyEcdsa.toStringRaw()}`;

    const privateKeyObject =
      accountUtils.getPrivateKeyObject(mainnetOperatorKey);

    newState.accounts['mainnet-operator'] = {
      accountId: mainnetOperatorId,
      privateKey: mainnetOperatorKey,
      network: 'mainnet',
      alias: 'mainnet-operator',
      type: 'ECDSA',
      publicKey: privateKeyObject.publicKey.toStringDer(),
      evmAddress: privateKeyObject.publicKey.toEvmAddress(),
      solidityAddress: `${AccountId.fromString(
        mainnetOperatorId,
      ).toSolidityAddress()}`,
      solidityAddressFull: `0x${AccountId.fromString(
        mainnetOperatorId,
      ).toSolidityAddress()}`,
    };
  }

  if (localnetOperatorId) {
    const privateLocalnetKeyEcdsa =
      PrivateKey.fromStringDer(localnetOperatorKey);
    newState.localnetOperatorKeyHex = `0x${privateLocalnetKeyEcdsa.toStringRaw()}`;

    const privateKeyObject =
      accountUtils.getPrivateKeyObject(localnetOperatorKey);

    newState.accounts['localnet-operator'] = {
      accountId: localnetOperatorId,
      privateKey: localnetOperatorKey,
      network: 'localnet',
      alias: 'localnet-operator',
      type: 'ECDSA',
      publicKey: privateKeyObject.publicKey.toStringDer(),
      evmAddress: privateKeyObject.publicKey.toEvmAddress(),
      solidityAddress: `${AccountId.fromString(
        localnetOperatorId,
      ).toSolidityAddress()}`,
      solidityAddressFull: `0x${AccountId.fromString(
        localnetOperatorId,
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
