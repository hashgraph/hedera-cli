package com.hedera.cli;

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
    Account account;

    @ShellMethod(value = "manage hedera account")
    public void account(
        @ShellOption(defaultValue = "") String subCommand,
        @ShellOption(defaultValue = "", arity = -1) String... args) {
        Account account = new Account();
        account.handle(subCommand, args);
    }

    @ShellMethod(value = "transfer hbars from one hedera account to another")
    public void transfer() {
        System.out.println("Stub function.");
    }

}