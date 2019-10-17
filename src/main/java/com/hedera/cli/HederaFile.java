package com.hedera.cli;

import java.util.ArrayList;
import java.util.Arrays;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.defaults.CliDefaults;
import com.hedera.cli.hedera.file.File;
import com.hedera.cli.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class HederaFile extends CliDefaults {

    @Autowired
    ShellHelper shellHelper;

    @Autowired
    ApplicationContext context;

    @Autowired
    InputReader inputReader;

    public HederaFile() {
    }

    @ShellMethodAvailability("isDefaultNetworkAndAccountSet")
    @ShellMethod(value = "manage hedera file")
    public void file(
            @ShellOption(defaultValue = "") String subCommand,
            // file create
            @ShellOption(value = { "-c", "--contentString" }, defaultValue = "") String c,
			@ShellOption(value = { "-d", "--date" }, defaultValue = "") String d,
            @ShellOption(value = { "-s", "--fileSizeByte" }, defaultValue = "") String s,
            @ShellOption(value = { "-t", "--maxTransactionFee" }, defaultValue = "") String t,
            // file delete
            @ShellOption(value = {"-f", "--fileID"}, defaultValue = "") String f) {
    
        String[] args = new String[]{};

        // @formatter:off
        if (subCommand.equals("create")) {
			ArrayList<String> argsList = new ArrayList<String>();
			if (!c.isEmpty()) argsList.add(c);
            if (!d.isEmpty()) argsList.add(d);
            if (!s.isEmpty()) argsList.add(s);
            if (!t.isEmpty()) argsList.add(t);
			Object[] objs = argsList.toArray();
			args = Arrays.copyOf(objs, objs.length, String[].class);
        }

        if (subCommand.equals("delete")) {
            ArrayList<String> argsList = new ArrayList<String>();
            if (!f.isEmpty()) argsList.add(f);
            Object[] objs = argsList.toArray();
			args = Arrays.copyOf(objs, objs.length, String[].class);
        }
        // @formatter:on
        
        File file = new File();
        file.handle(context, inputReader, subCommand, args);
    }
}