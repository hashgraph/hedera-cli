const stateController = require('../../state/stateController.js');

async function main() {
  const [deployer] = await ethers.getSigners();

  console.log('Deploying contracts with the account:', deployer.address);

  // The deployer will also be the owner of our token contract
  const MyToken = await ethers.getContractFactory('MyToken', deployer);
  const contract = await MyToken.deploy(deployer.address);

  console.log('Contract deployed at:', contract.target);

  // Store address in state memory as "erc20address"
  stateController.default.saveToMemory('erc20address', contract.target);
}

main().catch(console.error);
