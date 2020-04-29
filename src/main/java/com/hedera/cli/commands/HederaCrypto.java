package com.hedera.cli.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hedera.cli.hedera.crypto.Account;
import com.hedera.cli.hedera.crypto.Transfer;
import com.hedera.cli.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@ShellComponent
public class HederaCrypto extends CommandBase {

    @Autowired
    private ShellHelper shellHelper;

    @Autowired
    private Account account;

    @Autowired
    private Transfer transfer;

    @Getter
    private String[] transferArgs;

    @ShellMethodAvailability("isDefaultNetworkAndAccountSet")
    @ShellMethod(value = "manage Hedera account")
    public void account(@ShellOption(defaultValue = "") String subCommand,
                        // account use
                        @ShellOption(defaultValue = "") String accountId,
                        // Specifying -y flag will set y to be true (which will skip the preview)
                        @ShellOption(value = {"-y", "--skipPreview"}, arity = 0, defaultValue = "false") boolean y,
                        // account create
                        @ShellOption(value = {"-b", "--balance"}, defaultValue = "") String b,
                        // Specifying -k flag will set k to be false (and not create a new keypair)
                        @ShellOption(value = {"-k", "--keygen"}, arity = 0, defaultValue = "true") boolean k,
                        @ShellOption(value = {"-pk", "--publicKey"}, defaultValue = "") String pk,
                        // account delete
                        @ShellOption(value = {"-o", "--oldAccount"}, defaultValue = "") String o,
                        @ShellOption(value = {"-n", "--newAccount"}, defaultValue = "") String n) {

        // convert our Spring Shell arguments into an argument list that PicoCli can use.
        String[] args;
        List<String> argsList = new ArrayList<>();
        Object[] objs;

        switch (subCommand) {
            case "create":
                argsList.add("-k " + k);
                if (!b.isEmpty()) argsList.add("-b " + b);
                if (!pk.isEmpty()) argsList.add("-pk " + pk);
                break;
            case "use":
            case "default":
            case "recovery":
            case "balance":
            case "update":
            case "info":
                argsList = addAccountToArgsList(accountId, argsList);
                if (argsList.isEmpty()) {
                    shellHelper.printError("Please provide an account id");
                }
                break;
            case "delete":
                argsList.add("-y " + y);
                if (!o.isEmpty()) argsList.add("-o " + o);
                if (!n.isEmpty()) argsList.add("-n " + n);
                break;
            case "ls":
                break;
            default:
                break;
        }

        objs = argsList.toArray();
        args = Arrays.copyOf(objs, objs.length, String[].class);
        // Pass args onwards and invoke our PicoCli classes
        account.handle(subCommand, args);
    }

    public List<String> addAccountToArgsList(String accountId, List<String> argsList) {
        if (!accountId.isEmpty()) {
            argsList.add(accountId);
            return argsList;
        } else {
            return argsList;
        }
    }

    // @formatter:off
    @ShellMethodAvailability("isDefaultNetworkAndAccountSet")
    @ShellMethod(value = "transfer hbars from one Hedera account to another")
    public void transfer(@ShellOption(value = {"-s", "--sender"}, defaultValue = "") String[] sender,
                         @ShellOption(value = {"-r", "--recipient"}, defaultValue = "") String[] recipient,
                         @ShellOption(value = {"-y", "--skipPreview"}, arity = 0, defaultValue = "false") boolean y,
                         @ShellOption(value = {"-hb", "--hbars"}, defaultValue = "") String[] hb,
                         @ShellOption(value = {"-tb", "--tinybars"}, defaultValue = "") String[] tb) {
        // @formatter:on
        List<String> argsList = new ArrayList<>();

        if (isEmptyStringArray(recipient)) {
            shellHelper.printError("Recipients cannot be empty");
            transfer.run();
            return;
        }

        if (!isEmptyStringArray(hb) && !isEmptyStringArray(tb)) {
            shellHelper.printError("Transfer amounts must either be in hbars or tinybars, not both");
            return;
        }

        if (!isEmptyStringArray(hb)) { // hbar args
            argsList = createArgList(argsList, hb, sender, recipient, y, false);
        }

        if (!isEmptyStringArray(tb)) { // tinybar args
            argsList = createArgList(argsList, tb, sender, recipient, y, true);
        }

        setTransferArgs(argsList);

        // transfer is a picocli command object
        transfer.handle(transferArgs);
    }

    private void setTransferArgs(List<String> argsList) {
        Object[] objs = argsList.toArray();
        transferArgs = Arrays.copyOf(objs, objs.length, String[].class);
    }

    public List<String> createArgList(List<String> argsList, String[] amount,
                                      String[] sender, String[] recipient, boolean y, boolean isTiny) {

        argsList.add("-s=" + Arrays.toString(sender)
                .replace("[", "")
                .replace("]", "")
                .replace(" ", ""));
        argsList.add("-r=" + Arrays.toString(recipient)
                .replace("[", "")
                .replace("]", "")
                .replace(" ", ""));

        argsList.add("-y=" + y);

        if (isTiny) {
            // amount in tinybars
            argsList.add("-tb=" + Arrays.toString(amount)
                    .replace("[", "")
                    .replace("]", "")
                    .replace(" ", ""));
        } else {
            // amount in hbars
            argsList.add("-hb=" + Arrays.toString(amount)
                    .replace("[", "")
                    .replace("]", "")
                    .replace(" ", ""));
        }
        return argsList;
    }

    public boolean isEmptyStringArray(String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null) {
                return false;
            }
        }
        return true;
    }
}