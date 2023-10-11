const {
    PrivateKey,
    AccountCreateTransaction,
    Hbar,
    AccountId,
    Mnemonic
  } = require("@hashgraph/sdk");


async function main() {
    let newAccountEDPrivateKey = await PrivateKey.generateED25519Async();
    let newAccountEDPublicKey = newAccountEDPrivateKey.publicKey;
    
    let newAccountECPrivateKey = await PrivateKey.generateECDSAAsync();
    let newAccountECPublicKey = newAccountECPrivateKey.publicKey;
     
    console.log(newAccountEDPrivateKey.toString())
    console.log(newAccountEDPublicKey.toString())
    
    console.log(newAccountECPrivateKey.toString())
    console.log(newAccountECPublicKey.toString())

    // passphrase / mnemonic
    /*const mnemonic = "cart day miss lift car insane slogan upgrade symptom absorb already flush oyster basic seven";
    const recoveredKey = await Mnemonic.fromString(mnemonic);
    console.log(await recoveredMnemonic.toPrivateKey())*/
}

main();