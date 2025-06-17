//const stateController = require('../../state/stateController.js');

async function main() {
  const [deployer] = await ethers.getSigners();

  console.log('Deploying contracts with the account:', deployer.address);

  // Get the ContractFactory for
  //const ERC20Token = await ethers.getContractFactory('ERC20Token', deployer);
  //const contractAddress = stateController.default.get('erc20address'); // read from memory functie?

  //console.log('Contract deployed at:', contract.target);

  // Store address in state memory as "erc20address"
  //stateController.default.saveToMemory('erc20address', contract.target);
}

main().catch(console.error);
