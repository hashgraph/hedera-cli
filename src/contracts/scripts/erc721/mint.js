const stateController = require('../../../state/stateController.js').default;

/**
 * Purpose: Mint an ERC721 token and save its ID in the script arguments.
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
  const contractAddress = stateController.getScriptArgument('erc721address'); // read from arguments
  const contract = await ERC721Token.attach(contractAddress);

  // Mint a token to ourselves
  console.log('Minting token with the account:', deployer.address);
  const mintTx = await contract.safeMint(deployer.address);
  await mintTx.wait();
  console.log('Token minted');

  // Store address in script arguments as "erc721TokenId"
  stateController.saveScriptArgument('erc721TokenId', 0); // Assuming the first token ID is 0
}

main().catch(console.error);
