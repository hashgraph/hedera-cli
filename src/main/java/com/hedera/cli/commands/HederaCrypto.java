package com.hedera.cli.commands;

import java.util.ArrayList;
import java.util.Arrays;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.defaults.CliDefaults;
import com.hedera.cli.hedera.crypto.Account;
import com.hedera.cli.hedera.crypto.Transfer;
import com.hedera.cli.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@ShellComponent
public class HederaCrypto extends CliDefaults {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ShellHelper shellHelper;

    @Autowired
    private InputReader inputReader;

    @Autowired
    private Account account;

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
        ArrayList<String> argsList = new ArrayList<String>();
        Object[] objs = null;

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
    public void transfer(@ShellOption(defaultValue = "") String subCommand,
                         @ShellOption(value = {"-a", "--accountId"}, defaultValue = "") String[] aa,
                         @ShellOption(value = {"-r", "--recipientAmount"}, defaultValue = "") String[] rr,
                         @ShellOption(value = {"-n", "--noPreview"}, arity = 0) boolean n) {

        // @formatter:on
        ArrayList<String> argsList = new ArrayList<>();

        if (isEmptyStringArray(aa) || isEmptyStringArray(rr)) {
            shellHelper.printError("Recipient and amount cannot be empty");
        } else {
            argsList.add("-a=" + Arrays.toString(aa)
                    .replace("[", "")
                    .replace("]", "")
                    .replace(" ", ""));
            argsList.add("-r=" + Arrays.toString(rr)
                    .replace("[", "")
                    .replace("]", "")
                    .replace(" ", ""));
            argsList.add("-n=yes");
            if (n) {
                argsList.add("-n=no");
            }
        }

        Object[] objs = argsList.toArray();
        String[] args = Arrays.copyOf(objs, objs.length, String[].class);
        // @formatter:off

        Transfer transfer = new Transfer();
        try {
            transfer.handle(context, inputReader, subCommand, args);
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

//	public static String[] add(String[] originalArray, String newItem)
//	{
//		int currentSize = originalArray.length;
//		int newSize = currentSize + 1;
//		String[] tempArray = new String[ newSize ];
//		for (int i=0; i < currentSize; i++)
//		{
//			tempArray[i] = originalArray [i];
//		}
//		tempArray[newSize- 1] = newItem;
//		return tempArray;
//	}
}