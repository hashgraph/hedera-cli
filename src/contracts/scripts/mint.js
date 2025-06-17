const stateController = require('../../state/stateController.js');

/**
 * Purpose: Mint an ERC20 token and save its ID in state memory.
 *
 * Storage:
 *  -
 *
 * Read:
 *  - erc20address: The address of the deployed ERC20 token contract
 */
async function main() {
  const [deployer] = await ethers.getSigners();

  // Get the ContractFactory for
  const ERC20Token = await ethers.getContractFactory('ERC20Token', deployer);
  const contractAddress = stateController.default.getFromMemory('erc20address'); // read from memory functie?
  const contract = await ERC20Token.attach(contractAddress);

  // Mint a token to ourselves
  console.log('Minting token with the account:', deployer.address);
  const mintTx = await contract.safeMint(deployer.address);
  const receipt = await mintTx.wait();
  const mintedTokenId = receipt.logs[0].topics[3];
  console.log('Minted token ID:', mintedTokenId);

  // Check the balance of the token
  const balance = await contract.balanceOf(deployer.address);
  console.log('Balance:', balance.toString(), 'NFT');

  // Store address in state memory as "erc20TokenId"
  stateController.default.saveToMemory('erc20TokenId', mintedTokenId);
}

main().catch(console.error);
