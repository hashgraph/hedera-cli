package com.hedera.cli.hedera.file;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import java.util.Arrays;

@Command(name = "file", header = "Create, update, delete file.",
subcommands = {FileCreate.class, FileDelete.class})
// This subcommand here is not for the real subcommand handling it is only for documentation
public class File implements Runnable {

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public void handle(String subCommand, String... args) {
        CommandLine cmd = new CommandLine(new File());
        switch (subCommand) {
            case "create":
                System.out.println(Arrays.asList(args));
                CommandLine.usage(new FileCreate(), System.out);
                cmd.execute(args);
                break;
            case "delete":
                System.out.println(Arrays.asList(args));
                CommandLine.usage(new FileDelete(), System.out);
                cmd.execute(args);
                break;
            case "update":
                System.out.println("Not yet implemented");
                break;
            default:
                this.run();
                break;
        }
    }
}
