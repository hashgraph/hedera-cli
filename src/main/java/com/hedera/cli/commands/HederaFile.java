package com.hedera.cli.commands;

import java.util.ArrayList;
import java.util.Arrays;

import com.hedera.cli.hedera.file.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class HederaFile extends CommandBase {

    @Autowired
    private File file;

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
        if ("create".equals(subCommand)) {
			ArrayList<String> argsList = new ArrayList<String>();
			if (!c.isEmpty()) argsList.add(c);
            if (!d.isEmpty()) argsList.add(d);
            if (!s.isEmpty()) argsList.add(s);
            if (!t.isEmpty()) argsList.add(t);
			Object[] objs = argsList.toArray();
			args = Arrays.copyOf(objs, objs.length, String[].class);
        }

        if ("delete".equals(subCommand)) {
            ArrayList<String> argsList = new ArrayList<String>();
            if (!f.isEmpty()) argsList.add(f);
            Object[] objs = argsList.toArray();
			args = Arrays.copyOf(objs, objs.length, String[].class);
        }
        // @formatter:on
        
        file.handle(subCommand, args);
    }
}