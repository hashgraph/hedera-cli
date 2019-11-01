package com.hedera.cli.commands;

import java.util.ArrayList;
import java.util.Arrays;

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
        String[] args = new String[]{};
        ArrayList<String> argsList = new ArrayList<>();
        Object[] objs;

        switch (subCommand) {
            case "create":
                if (k) argsList.add("-k");
                if (!b.isEmpty()) argsList.add("-b " + b);
                objs = argsList.toArray();
                args = Arrays.copyOf(objs, objs.length, String[].class);
                break;
            case "update":
                break;
            case "info":
                if (!accountId.isEmpty()) argsList.add(accountId);
                objs = argsList.toArray();
                args = Arrays.copyOf(objs, objs.length, String[].class);
                break;
            case "delete":
                if (y) argsList.add("-y");
                if (!o.isEmpty()) argsList.add("-o " + o);
                if (!n.isEmpty()) argsList.add("-n " + n);
                objs = argsList.toArray();
                args = Arrays.copyOf(objs, objs.length, String[].class);
                break;
            case "recovery":
                if (!accountId.isEmpty()) argsList.add(accountId);
                objs = argsList.toArray();
                args = Arrays.copyOf(objs, objs.length, String[].class);
                break;
            case "ls":
                break;
            case "use":
                if (!accountId.isEmpty()) argsList.add(accountId);
                objs = argsList.toArray();
                args = Arrays.copyOf(objs, objs.length, String[].class);
                break;
            case "balance":
                if (!accountId.isEmpty()) argsList.add(accountId);
                objs = argsList.toArray();
                args = Arrays.copyOf(objs, objs.length, String[].class);
                break;
            default:
                break;
        }
        // Pass args onwards and invoke our PicoCli classes
        account.handle(inputReader, subCommand, args);
    }

    @ShellMethodAvailability("isDefaultNetworkAndAccountSet")
    @ShellMethod(value = "transfer hbars from one Hedera account to another")
    public void transfer(@ShellOption(value = {"-s", "--sender"}, arity = 1, defaultValue = "") String[] sender,
                         @ShellOption(value = {"-r", "--recipient"}, arity = 1, defaultValue = "") String[] recipient,
                         @ShellOption(value = {"-y", "--yesSkipPreview"}, arity = 0) boolean y,
                         @ShellOption(value = {"-hb", "--tinybars"}, defaultValue = "") String[] hb,
                         @ShellOption(value = {"-tb", "--hbars"}, defaultValue = "") String[] tb) {

        // TODO check that sender is not empty before proceeding

        // @formatter:on
        ArrayList<String> argsList = new ArrayList<>();

        if (!isEmptyStringArray(recipient) && !isEmptyStringArray(hb) && !isEmptyStringArray(tb)) {
            shellHelper.printError("Amount must be in hbar or tinybar");
        }
        if (isEmptyStringArray(recipient) && (isEmptyStringArray(hb) && isEmptyStringArray(tb))) {
            shellHelper.printError("Recipient and amount cannot be empty");
        }
        if (!isEmptyStringArray(recipient) && !isEmptyStringArray(hb) && isEmptyStringArray(tb)) {
            // hbar args
            argsList.add("-r=" + Arrays.toString(recipient)
                    .replace("[", "")
                    .replace("]", "")
                    .replace(" ", ""));
            argsList.add("-s=" + Arrays.toString(sender)
                    .replace("[", "")
                    .replace("]", "")
                    .replace(" ", ""));
            argsList.add("-hb=" + Arrays.toString(hb)
                    .replace("[", "")
                    .replace("]", "")
                    .replace(" ", ""));
            if (y) {
                argsList.add("-y=no");
            } else {
                argsList.add("-y=yes");
            }
        }
        if (!isEmptyStringArray(recipient) && isEmptyStringArray(hb) && !isEmptyStringArray(tb)) {
            // tinybar args
            argsList.add("-r=" + Arrays.toString(recipient)
                    .replace("[", "")
                    .replace("]", "")
                    .replace(" ", ""));
            argsList.add("-s=" + Arrays.toString(sender)
                    .replace("[", "")
                    .replace("]", "")
                    .replace(" ", ""));
            argsList.add("-tb=" + Arrays.toString(tb)
                    .replace("[", "")
                    .replace("]", "")
                    .replace(" ", ""));
            if (y) {
                argsList.add("-y=no");
            } else {
                argsList.add("-y=yes");
            }
        }

        Object[] objs = argsList.toArray();
        String[] args = Arrays.copyOf(objs, objs.length, String[].class);
        // @formatter:off

        try {
            transfer.handle(inputReader, args);
        } catch (Exception e) {
            e.printStackTrace();
            // print out a useful message for end user here
        }
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