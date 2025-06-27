const stateController = require('../../../state/stateController.js').default;

/**
 * Purpose: Deploys an ERC721 token contract and saves its address in the script arguments.
 *
 * Storage:
 *  - accountstorageaddress: The address of the deployed HederaAccountStorage contract
 *
 * Read: /
 */
async function main() {
  // The deployer will also be the owner of our token contract
  const HederaAccountStorage = await ethers.getContractFactory(
    'HederaAccountStorage',
  );
  const contract = await HederaAccountStorage.deploy();
  await contract.waitForDeployment();

  const contractAddress = await contract.getAddress();
  console.log('HederaAccountStorage contract deployed at:', contractAddress);

  // Store address in script arguments as "accountstorageaddress"
  stateController.saveScriptArgument('accountstorageaddress', contractAddress);
}

main().catch(console.error);
