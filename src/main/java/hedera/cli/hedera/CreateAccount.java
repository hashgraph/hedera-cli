package hedera.cli.hedera;

import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.HederaException;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import io.github.cdimascio.dotenv.Dotenv;
import org.jline.terminal.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.math.BigInteger;
import java.util.Map;

@ShellComponent
public class CreateAccount {

    @Autowired
    @Lazy
    private Terminal terminal;

    public static Client hederaClient() {

        // Grab configuration variables from the .env file

        var operatorId = AccountId.fromString(Dotenv.load().get("OPERATOR_ID"));
        var operatorKey = Ed25519PrivateKey.fromString(Dotenv.load().get("OPERATOR_KEY"));
        var nodeId = AccountId.fromString(Dotenv.load().get("NODE_ID"));
        var nodeAddress = Dotenv.load().get("NODE_ADDRESS");

        // Build client

        var hederaClient = new Client(Map.of(nodeId, nodeAddress));

        // Set the the account ID and private key of the operator

        hederaClient.setOperator(operatorId, operatorKey);

        return hederaClient;
    }

    @ShellMethod("Create account")
    public String createaccount() throws HederaException {

        try{
        // 1. Generate a Ed25519 private, public key pair
        var newKey = Ed25519PrivateKey.generate();
        var newPublicKey = newKey.getPublicKey();

        // 2. Initialize Hedera client
        var client = hederaClient();

        // 3. Create new account on Hedera
        // In TINYBARS :D
        var amount = new BigInteger("500000000"); //5hbars
        var initialBalance = amount.longValue();
        var newAccountId = client.createAccount(newPublicKey, initialBalance).toString();

        // 4. Check new account balance

        var accountBalance = client.getAccountBalance(AccountId.fromString(newAccountId));
        return String.format(
                "private key = " + newKey,
                "public key = " + newPublicKey,
                newAccountId,
                accountBalance
        );
        } catch (HederaException e) {
            return "Something went wrong";
        }
    }
}