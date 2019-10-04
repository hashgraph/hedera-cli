package com.hedera.cli;

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

@ShellComponent
public class HederaCrypto extends CliDefaults {

    @Autowired
    ApplicationContext context;

    @Autowired
    ShellHelper shellHelper;

    @Autowired
    InputReader inputReader;

    @Autowired
    Account account;

    public HederaCrypto() {
    }

    @ShellMethodAvailability("isDefaultNetworkAndAccountSet")
    @ShellMethod(value = "manage Hedera account")
    public void account(@ShellOption(defaultValue = "") String subCommand,
            @ShellOption(defaultValue = "", arity = -1) String... args) {
        Account account = new Account();
        account.handle(context, inputReader, subCommand, args);
    }

    @ShellMethodAvailability("isDefaultNetworkAndAccountSet")
    @ShellMethod(value = "transfer hbars from one hedera account to another")
    public void transfer(@ShellOption(defaultValue = "") String subCommand,
            @ShellOption(defaultValue = "", arity = -1) String... args) {
        Transfer transfer = new Transfer();
        try {
            transfer.handle(context, inputReader, subCommand, args);
        } catch (Exception e) {
            e.printStackTrace();
            // print out a useful message for end user here
        }
    }

}