const stateController = require('../../state/stateController.js').default;

/**
 * Purpose: Mint an ERC721 token and save its ID in state memory.
 *
 * Storage:
 *  - erc721TokenId: The ID of the minted ERC721 token
 *
 * Read:
 *  - erc721address: The address of the deployed ERC721 token contract
 */
async function main() {
  const [deployer] = await ethers.getSigners();

  // Get the ContractFactory for
  const ERC721Token = await ethers.getContractFactory('ERC721Token', deployer);
  const contractAddress = stateController.getScriptArgument('erc721address'); // read from script arguments ERC721 address
  const contract = await ERC721Token.attach(contractAddress);

  // Check the balance of the token
  console.log('Checking balance of the account:', deployer.address);
  const balance = await contract.balanceOf(deployer.address);
  console.log('Balance:', balance.toString(), 'NFT');

  // Store address in script arguments as "erc721TokenId"
  stateController.saveScriptArgument('erc721TokenId', 0); // Assuming the first token ID is 0
}

main().catch(console.error);
