package com.hedera.cli;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.crypto.Account;
import com.hedera.cli.hedera.crypto.Transfer;
import com.hedera.cli.shell.ShellHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class HederaCrypto {

    @Autowired
    ShellHelper shellHelper;

    @Autowired
    InputReader inputReader;

    @Autowired
    Account account;

    @ShellMethod(value = "manage Hedera account")
    public void account(
        @ShellOption(defaultValue = "") String subCommand,
        @ShellOption(defaultValue = "", arity = -1) String... args) {
        Account account = new Account();
        account.handle(inputReader, subCommand, args);
    }

    @ShellMethod(value = "transfer hbars from one hedera account to another")
    public void transfer(
        @ShellOption(defaultValue = "") String subCommand,
        @ShellOption(defaultValue = "", arity = -1) String... args) {
        Transfer transfer = new Transfer();
        transfer.handle(subCommand, args);
    }

}