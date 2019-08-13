package com.hedera.cli.hedera.crypto;

// import com.hedera.hashgraph.sdk.account.AccountId;
// import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
// import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PublicKey;
// import io.github.cdimascio.dotenv.Dotenv;
import org.junit.Test;

public class AccountCreateTest {

    @Test
    public void testCreateNewAccount() {
        // we are transiting out from dotenv, to load private and public keys from an account's json
        // var privateKey = Ed25519PrivateKey.fromString(Dotenv.load().get("KEYGEN_MOBILE_PRIVATE_KEY"));
        // var publicKey = Ed25519PublicKey.fromString(Dotenv.load().get("KEYGEN_MOBILE_PUBLIC_KEY"));

        // AccountCreate accountCreate = new AccountCreate();
        // AccountId accountId = accountCreate.createNewAccount(privateKey, publicKey);
        // System.out.println(accountId);
    }
}
