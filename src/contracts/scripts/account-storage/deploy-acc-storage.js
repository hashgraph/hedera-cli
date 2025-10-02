const { saveScriptArgument } = require('../../../state/newStore.js');

/**
 * Purpose: Deploys an HederaAccountStorage contract and saves its address in the script arguments.
 *
 * Storage:
 *  - accountstorageaddress: The address of the deployed HederaAccountStorage contract
 *
 * Read: /
 */
async function main() {
  const HederaAccountStorage = await ethers.getContractFactory(
    'HederaAccountStorage',
  );
  const contract = await HederaAccountStorage.deploy();
  await contract.waitForDeployment();

  const contractAddress = await contract.getAddress();
  console.log('HederaAccountStorage contract deployed at:', contractAddress);

  // Store address in script arguments as "accountstorageaddress"
  saveScriptArgument('accountstorageaddress', contractAddress);
}

main().catch(console.error);
