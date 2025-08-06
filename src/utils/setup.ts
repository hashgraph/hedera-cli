import { AccountId, PrivateKey } from '@hashgraph/sdk';
import stateController from '../state/stateController';
import accountUtils from './account';
import { Account } from '../../types';

// rewrite the function below to create an a single operator account for each network with the network name as a prefix
export function setupOperatorAccount(
  operatorId: string,
  operatorKey: string,
  network: string,
): void {
  const state = stateController.getAll();
  let newState = { ...state };
  const privateKeyObject = accountUtils.getPrivateKeyObject(operatorKey);

  newState.accounts[`${network}-operator`] = {
    accountId: operatorId,
    privateKey: operatorKey,
    hexKey: `0x${PrivateKey.fromStringDer(operatorKey).toStringRaw()}`,
    network: network,
    name: `${network}-operator`,
    type: 'ECDSA',
    publicKey: privateKeyObject.publicKey.toStringDer(),
    evmAddress: privateKeyObject.publicKey.toEvmAddress(),
    evmKey: `0x${privateKeyObject.toStringRaw()}`,
    solidityAddress: `${AccountId.fromString(operatorId).toSolidityAddress()}`,
    solidityAddressFull: `0x${AccountId.fromString(operatorId).toSolidityAddress()}`,
  } as Account;
  // save the network in the state
  newState.network = network;
  stateController.saveState(newState);
}

/**
 * @description Setup operator accounts for previewnet, testnet, and mainnet in the state file
 */
// function setupOperatorAccounts(
//   testnetOperatorId: string,
//   testnetOperatorKey: string,
//   mainnetOperatorId: string,
//   mainnetOperatorKey: string,
//   previewnetOperatorId: string,
//   previewnetOperatorKey: string,
//   localnetOperatorId: string,
//   localnetOperatorKey: string,
// ): void {
//   const state = stateController.getAll();
//   let newState = { ...state };
//   newState.testnetOperatorKey = testnetOperatorKey;
//   newState.testnetOperatorId = testnetOperatorId;
//   newState.mainnetOperatorKey = mainnetOperatorKey;
//   newState.mainnetOperatorId = mainnetOperatorId;
//   newState.previewnetOperatorId = previewnetOperatorId;
//   newState.previewnetOperatorKey = previewnetOperatorKey;
//   newState.localnetOperatorKey = localnetOperatorKey;
//   newState.localnetOperatorId = localnetOperatorId;
//
//   if (testnetOperatorId) {
//     const privateTestnetKeyEcdsa = PrivateKey.fromStringDer(testnetOperatorKey);
//     newState.testnetOperatorKeyHex = `0x${privateTestnetKeyEcdsa.toStringRaw()}`;
//
//     const privateKeyObject =
//       accountUtils.getPrivateKeyObject(testnetOperatorKey);
//
//     newState.accounts['testnet-operator'] = {
//       accountType: AccountType.OPERATOR,
//       accountId: testnetOperatorId,
//       privateKey: testnetOperatorKey,
//       network: 'testnet',
//       name: 'testnet-operator',
//       type: 'ECDSA',
//       publicKey: privateKeyObject.publicKey.toStringDer(),
//       evmAddress: privateKeyObject.publicKey.toEvmAddress(),
//       evmKey: `0x${privateKeyObject.toStringRaw()}`,
//       solidityAddress: `${AccountId.fromString(
//         testnetOperatorId,
//       ).toSolidityAddress()}`,
//       solidityAddressFull: `0x${AccountId.fromString(
//         testnetOperatorId,
//       ).toSolidityAddress()}`,
//     } as Account;
//   }
//
//   if (previewnetOperatorId) {
//     const privatePreviewnetKeyEcdsa = PrivateKey.fromStringDer(
//       previewnetOperatorKey,
//     );
//     newState.previewnetOperatorKeyHex = `0x${privatePreviewnetKeyEcdsa.toStringRaw()}`;
//
//     const privateKeyObject = accountUtils.getPrivateKeyObject(
//       previewnetOperatorKey,
//     );
//
//     newState.accounts['preview-operator'] = {
//       accountId: previewnetOperatorId,
//       privateKey: previewnetOperatorKey,
//       network: 'previewnet',
//       name: 'preview-operator',
//       type: 'ECDSA',
//       publicKey: privateKeyObject.publicKey.toStringDer(),
//       evmAddress: privateKeyObject.publicKey.toEvmAddress(),
//       evmKey: `0x${privateKeyObject.toStringRaw()}`,
//       solidityAddress: `${AccountId.fromString(
//         previewnetOperatorId,
//       ).toSolidityAddress()}`,
//       solidityAddressFull: `0x${AccountId.fromString(
//         previewnetOperatorId,
//       ).toSolidityAddress()}`,
//     };
//   }
//
//   if (mainnetOperatorId) {
//     const privateMainnetKeyEcdsa = PrivateKey.fromStringDer(mainnetOperatorKey);
//     newState.mainnetOperatorKeyHex = `0x${privateMainnetKeyEcdsa.toStringRaw()}`;
//
//     const privateKeyObject =
//       accountUtils.getPrivateKeyObject(mainnetOperatorKey);
//
//     newState.accounts['mainnet-operator'] = {
//       accountId: mainnetOperatorId,
//       privateKey: mainnetOperatorKey,
//       network: 'mainnet',
//       name: 'mainnet-operator',
//       type: 'ECDSA',
//       publicKey: privateKeyObject.publicKey.toStringDer(),
//       evmAddress: privateKeyObject.publicKey.toEvmAddress(),
//       evmKey: `0x${privateKeyObject.toStringRaw()}`,
//       solidityAddress: `${AccountId.fromString(
//         mainnetOperatorId,
//       ).toSolidityAddress()}`,
//       solidityAddressFull: `0x${AccountId.fromString(
//         mainnetOperatorId,
//       ).toSolidityAddress()}`,
//     };
//   }
//
//   if (localnetOperatorId) {
//     const privateLocalnetKeyEcdsa =
//       PrivateKey.fromStringDer(localnetOperatorKey);
//     newState.localnetOperatorKeyHex = `0x${privateLocalnetKeyEcdsa.toStringRaw()}`;
//
//     const privateKeyObject =
//       accountUtils.getPrivateKeyObject(localnetOperatorKey);
//
//     newState.accounts['localnet-operator'] = {
//       accountId: localnetOperatorId,
//       privateKey: localnetOperatorKey,
//       network: 'localnet',
//       name: 'localnet-operator',
//       type: 'ECDSA',
//       publicKey: privateKeyObject.publicKey.toStringDer(),
//       evmAddress: privateKeyObject.publicKey.toEvmAddress(),
//       evmKey: `0x${privateKeyObject.toStringRaw()}`,
//       solidityAddress: `${AccountId.fromString(
//         localnetOperatorId,
//       ).toSolidityAddress()}`,
//       solidityAddressFull: `0x${AccountId.fromString(
//         localnetOperatorId,
//       ).toSolidityAddress()}`,
//     };
//   }
//
//   newState.network = 'testnet';
//
//   stateController.saveState(newState);
// }

// const setupUtils = {
//   setupOperatorAccount,
// };
//
// export default setupUtils;
