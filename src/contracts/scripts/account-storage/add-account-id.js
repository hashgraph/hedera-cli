const stateController = require('../../../state/stateController.js').default;

/**
 * Purpose: Store an account ID in the HederaAccountStorage contract.
 *
 * Storage: /
 *
 * Read:
 * - accountstorageaddress: The address of the deployed HederaAccountStorage contract
 * - aliceAccId: The account ID to be stored in the contract (from the account create command)
 */
async function main() {
  const accountIdToStore = stateController.getScriptArgument('aliceAccId');
  const contractAddress = stateController.getScriptArgument(
    'accountstorageaddress',
  );

  const HederaAccountStorage = await ethers.getContractFactory(
    'HederaAccountStorage',
  );
  const contract = await HederaAccountStorage.attach(contractAddress);

  // Store the account ID in the contract
  const tx = await contract.addAccountId(accountIdToStore);

  // Wait for the transaction to be mined
  await tx.wait();

  console.log(`Account ID ${accountIdToStore} successfully stored.`);
}

main().catch(console.error);
