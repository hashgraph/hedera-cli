package com.hedera.cli.hedera.crypto;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.services.HederaGrpc;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;

import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PublicKey;
import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Getter
@Setter
@Component
@Command(name = "update", description = "@|fg(225) Updates the account's keypair|@")
public class AccountUpdate implements Runnable, Operation {

    @Autowired
    private ShellHelper shellHelper;

    @Autowired
    private InputReader inputReader;

    @Autowired
    private HederaGrpc hederaGrpc;

    @Parameters(index = "0", description = "Hedera account in the format shardNum.realmNum.accountNum"
            + "%n@|bold,underline Usage:|@%n" + "@|fg(yellow) account update 0.0.1003|@")
    private String accountIdInString;

    @Override
    public void run() {
        AccountId accountId;
        try {
            accountId = AccountId.fromString(accountIdInString);
        } catch (Exception e) {
            shellHelper.printError("Invalid account id provided");
            return;
        }
        String privateKey = inputReader.prompt("Enter the NEW private key that will be used to update " + accountId, "secret", false);
        if (StringUtil.isNullOrEmpty(privateKey)) {
            shellHelper.printError("Enter the new public key to update the current account keys");
            return;
        }
        Ed25519PrivateKey newKey = Ed25519PrivateKey.fromString(privateKey);

        String originalPrivateKey = inputReader.prompt("Enter the ORIGINAL private key of " + accountId + " that will be changed", "secret", false);
        if (StringUtil.isNullOrEmpty(originalPrivateKey)) {
            shellHelper.printError("Enter the original public key to update the current account keys");
            return;
        }
        Ed25519PrivateKey originalKey = Ed25519PrivateKey.fromString(originalPrivateKey);

        boolean correctInfo = promptPreview(accountId, newKey.getPublicKey(), originalKey.getPublicKey());
        if (correctInfo) {
            shellHelper.print("Info is correct, let's go!");
            hederaGrpc.executeAccountUpdate(accountId, newKey, originalKey);
        } else {
            shellHelper.printError("Nope, incorrect, let's make some changes");
        }
    }

    private boolean promptPreview(AccountId accountId, Ed25519PublicKey newPublicKey, Ed25519PublicKey orignalPublicKey) {
        String choice = inputReader.prompt(
                "\nAccount to be updated: " + accountId + "\nPublic key of account will be updated from: "
                        + orignalPublicKey + "\nto new public key: " + newPublicKey
                        + "\n\nIs this correct?" + "\nyes/no");
        return choice.equalsIgnoreCase("yes") || choice.equalsIgnoreCase("y");
    }

    @Override
    public void executeSubCommand(InputReader inputReader, String... args) {
        if (args.length == 0) {
            CommandLine.usage(this, System.out);
        } else {
            try {
                new CommandLine(this).execute(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
