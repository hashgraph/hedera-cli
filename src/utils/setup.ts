import stateController from '../state/stateController';

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
  console.log(testnetOperatorKey)
  const state = stateController.getAll();
  let newState = { ...state };
  newState.testnetOperatorKey = testnetOperatorKey;
  newState.testnetOperatorId = testnetOperatorId;
  newState.mainnetOperatorKey = mainnetOperatorKey;
  newState.mainnetOperatorId = mainnetOperatorId;
  newState.previewnetOperatorId = previewnetOperatorId;
  newState.previewnetOperatorKey = previewnetOperatorKey;

  newState.network = 'testnet';

  stateController.saveState(newState);
}

const setupUtils = {
  setupOperatorAccounts,
};

export default setupUtils;
