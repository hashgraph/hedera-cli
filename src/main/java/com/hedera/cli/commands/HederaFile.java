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
            @ShellOption(value = {"-s", "--fileSizeByte"}, defaultValue = "") String s) {

        String[] args;
        List<String> argsList = new ArrayList<>();
        Object[] objects;

        // @formatter:off
        if ("create".equals(subCommand)) {
            if (!c.isEmpty()) argsList.add("-c=" + c);
            if (!s.isEmpty()) argsList.add("-s=" + s);
            if (!d.isEmpty()) argsList.add("-d=" + d
                    .replace("[", "")
                    .replace("]", "")
                    .replace(" ", ""));
            System.out.println(argsList);
        }

        if ("delete".equals(subCommand)) {
            argsList = addFileToArgsList(fileId, argsList);
            if (argsList.isEmpty()) {
                shellHelper.printError("Please provide a file id");
            }
        }

        if ("info".equals(subCommand)) {
            argsList = addFileToArgsList(fileId, argsList);
            if (argsList.isEmpty()) {
                shellHelper.printError("Please provide a file id");
            }
        }
        // @formatter:on
        objects = argsList.toArray();
        args = Arrays.copyOf(objects, objects.length, String[].class);
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