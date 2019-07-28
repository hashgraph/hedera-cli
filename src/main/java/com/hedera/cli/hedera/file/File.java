package com.hedera.cli.hedera.file;

import org.springframework.shell.standard.ShellOption;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.Arrays;

@Command(name = "file", header = "Create, update, delete file.",
subcommands = {FileCreate.class, FileDelete.class})
public class File implements Runnable {

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public void handle(String subCommand, String... args) {
        switch (subCommand) {
            case "create":
                new CommandLine(new FileCreate()).execute(args);
                break;
            case "delete":
                this.handleDelete(subCommand, args);
                break;
            case "update":
                System.out.println("Not yet implemented");
                break;
            default:
                this.run();
                break;
        }
    }
        private void handleDelete(
                @ShellOption(arity = 2) String subCommand, String... args) {

            System.out.println("How are the args consumed");
            System.out.println(Arrays.asList(args));
            CommandLine commandLine = new CommandLine(new FileDelete());
            commandLine.parseArgs(args);
//            new CommandLine(new FileDelete()).execute(args);
    }
}
