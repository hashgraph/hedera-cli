package com.hedera.cli.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hedera.cli.defaults.CliDefaults;
import com.hedera.cli.hedera.file.File;

import com.hedera.cli.shell.ShellHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class HederaFile extends CliDefaults {

    @Autowired
    private File file;

    @Autowired
    private ShellHelper shellHelper;

    @ShellMethodAvailability("isDefaultNetworkAndAccountSet")
    @ShellMethod(value = "manage hedera file")
    public void file(
            @ShellOption(defaultValue = "") String subCommand,
            @ShellOption(defaultValue = "") String fileId,
            // file create
            @ShellOption(value = {"-c", "--contentString"}, defaultValue = "") String c,
            @ShellOption(value = {"-d", "--date"}, defaultValue = "") String d,
            @ShellOption(value = {"-s", "--fileSizeByte"}, defaultValue = "") String s,
            @ShellOption(value = {"-t", "--maxTransactionFee"}, defaultValue = "") String t) {

        String[] args = new String[]{};

        // @formatter:off
        if ("create".equals(subCommand)) {
            List<String> argsList = new ArrayList<String>();
            if (!c.isEmpty()) argsList.add(c);
            if (!d.isEmpty()) argsList.add(d);
            if (!s.isEmpty()) argsList.add(s);
            if (!t.isEmpty()) argsList.add(t);
            Object[] objs = argsList.toArray();
            args = Arrays.copyOf(objs, objs.length, String[].class);
        }

        if ("delete".equals(subCommand)) {
            List<String> argsList = new ArrayList<String>();
            argsList = addFileToArgsList(fileId, argsList);
            if (argsList.isEmpty()) {
                shellHelper.printError("Please provide a file id");
            }
            Object[] objs = argsList.toArray();
            args = Arrays.copyOf(objs, objs.length, String[].class);
        }

        if ("info".equals(subCommand)) {
            List<String> argsList = new ArrayList<>();
            argsList = addFileToArgsList(fileId, argsList);
            if (argsList.isEmpty()) {
                shellHelper.printError("Please provide a file id");
            }
            Object[] objects = argsList.toArray();
            args = Arrays.copyOf(objects, objects.length, String[].class);
        }
        // @formatter:on

        file.handle(subCommand, args);
    }

    public List<String> addFileToArgsList(String fileId, List<String> argsList) {
        if (!fileId.isEmpty()) {
            argsList.add(fileId);
            return argsList;
        } else {
            return argsList;
        }
    }
}