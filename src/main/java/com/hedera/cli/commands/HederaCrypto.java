package com.hedera.cli.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.defaults.CliDefaults;
import com.hedera.cli.hedera.crypto.Account;
import com.hedera.cli.hedera.crypto.Transfer;
import com.hedera.cli.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@ShellComponent
public class HederaCrypto extends CliDefaults {

    @Autowired
    private ShellHelper shellHelper;

    @Autowired
    private InputReader inputReader;

    @Autowired
    private Account account;

    @Autowired
    private Transfer transfer;

    @ShellMethodAvailability("isDefaultNetworkAndAccountSet")
    @ShellMethod(value = "manage Hedera account")
    public void account(@ShellOption(defaultValue = "") String subCommand,
                        // account use
                        @ShellOption(defaultValue = "") String accountId,
                        // Specifying -y flag will set y to be true (which will skip the preview)
                        @ShellOption(value = {"-y", "--yes"}, defaultValue = "false") boolean y,
                        // account create
                        @ShellOption(value = {"-b", "--balance"}, defaultValue = "") String b,
                        @ShellOption(value = {"-k", "--keygen"}, defaultValue = "true") boolean k,
                        @ShellOption(value = {"-m", "--method"}, defaultValue = "") String m,
                        @ShellOption(value = {"-r", "--record"}, defaultValue = "false") boolean r,
                        // account delete
                        @ShellOption(value = {"-o", "--oldAccount"}, defaultValue = "") String o,
                        @ShellOption(value = {"-n", "--newAccount"}, defaultValue = "") String n) {

        // convert our Spring Shell arguments into an argument list that PicoCli can use.
        String[] args;
        List<String> argsList = new ArrayList<>();
        Object[] objs;

        switch (subCommand) {
            case "create":
                if (k) argsList.add("-k");
                if (!b.isEmpty()) argsList.add("-b " + b);
                break;
            case "update":
                shellHelper.printError("To be implemented");
                break;
            case "info":
            case "recovery":
            case "use":
            case "balance":
                argsList = addAccountToArgsList(accountId, argsList);
                if (argsList.isEmpty()) {
                    shellHelper.printError("Input an account Id");
                }
                break;
            case "delete":
                if (y) argsList.add("-y");
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
        account.handle(inputReader, subCommand, args);
    }

    public List<String> addAccountToArgsList(String accountId, List<String> argsList) {
        if (!accountId.isEmpty()) {
            argsList.add(accountId);
            return argsList;
        } else {
            return argsList;
        }
    }

    @ShellMethodAvailability("isDefaultNetworkAndAccountSet")
    @ShellMethod(value = "transfer hbars from one Hedera account to another")
    public void transfer(@ShellOption(value = {"-s", "--sender"}, defaultValue = "") String[] sender,
                         @ShellOption(value = {"-r", "--recipient"}, defaultValue = "") String[] recipient,
                         @ShellOption(value = {"-y", "--yesSkipPreview"}, arity = 0) boolean y,
                         @ShellOption(value = {"-hb", "--hbars"}, defaultValue = "") String[] hb,
                         @ShellOption(value = {"-tb", "--tinybars"}, defaultValue = "") String[] tb) {

        // @formatter:on
        List<String> argsList = new ArrayList<>();

        if (isEmptyStringArray(recipient)) {
            shellHelper.printError("Recipient cannot be empty");
            return;
        }
        if (!isEmptyStringArray(hb)) {
            if (!isEmptyStringArray(tb)) {
                shellHelper.printError("Amount must be in hbar or tinybar");
                return;
            } else {
                // hbar args
                argsList = createArgList(argsList, hb, sender, recipient, y, false);
            }
        }
        if (isEmptyStringArray(hb)) {
            if (isEmptyStringArray(tb)) {
                shellHelper.printError("Amount cannot be empty");
                return;
            } else {
                // tinybar args
                argsList = createArgList(argsList, tb, sender, recipient, y, true);
            }
        }

        Object[] objs = argsList.toArray();
        String[] args = Arrays.copyOf(objs, objs.length, String[].class);
        // @formatter:off
        transfer.handle(inputReader, args);
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
        if (y) {
            argsList.add("-y=no");
        } else {
            argsList.add("-y=yes");
        }

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