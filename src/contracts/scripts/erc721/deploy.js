const stateController = require('../../../state/stateController.js').default;

/**
 * Purpose: Deploys an ERC721 token contract and saves its address in the script arguments.
 *
 * Storage:
 *  - erc721address: The address of the deployed ERC20 token contract
 *
 * Read: /
 */
async function main() {
  const [deployer] = await ethers.getSigners();

  console.log('Deploying contracts with the account:', deployer.address);

  // The deployer will also be the owner of our token contract
  const ERC721Token = await ethers.getContractFactory('ERC721Token', deployer);
  const contract = await ERC721Token.deploy(deployer.address);
  await contract.waitForDeployment();

  const contractAddress = await contract.getAddress();
  console.log('ERC721 Token contract deployed at:', contractAddress);

  // Store address in script arguments as "erc721address"
  stateController.saveScriptArgument('erc721address', contractAddress);
}

main().catch(console.error);
