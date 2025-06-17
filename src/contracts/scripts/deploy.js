const stateController = require('../../state/stateController.js');

/**
 * Purpose: Deploys an ERC20 token contract and saves its address in state memory.
 * 
 * Storage:
 *  - erc20address: The address of the deployed ERC20 token contract
 * 
 * Read: /
 */
async function main() {
  const [deployer] = await ethers.getSigners();

  console.log('Deploying contracts with the account:', deployer.address);

  // The deployer will also be the owner of our token contract
  const ERC20Token = await ethers.getContractFactory('ERC20Token', deployer);
  const contract = await ERC20Token.deploy(deployer.address);

  console.log('Contract deployed at:', contract.target);

  // Store address in state memory as "erc20address"
  stateController.default.saveToMemory('erc20address', contract.target);
}

main().catch(console.error);
