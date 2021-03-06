package com.hedera.cli.commands;

import java.util.ArrayList;
import java.util.Arrays;

import com.hedera.cli.hedera.network.Network;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class HederaNetwork {

    @Autowired
    private Network network;

    @ShellMethod(value = "switch and manage different Hedera network")
    public void network(
            @ShellOption(defaultValue = "") String subCommand,
            @ShellOption(defaultValue = "") String n) {

        String[] args = new String[]{};
        ArrayList<String> argsList = new ArrayList<>();
        Object[] objs;

        switch (subCommand) {
            case "ls":
                break;
            case "use":
                if (!n.isEmpty()) argsList.add(n);
                objs = argsList.toArray();
                args = Arrays.copyOf(objs, objs.length, String[].class);
                break;
            default:
                break;
        }
        // Pass args onwards and invoke our PicoCli classes
        network.handle(subCommand, args);
    }
}