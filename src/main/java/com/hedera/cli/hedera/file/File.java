package com.hedera.cli.hedera.file;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import java.util.Arrays;

@Command(name = "file", description = "@|fg(225) Create, update, delete file.|@"
        + "%n@|fg(yellow) file create <args> OR"
        + "%nfile update <args> OR"
        + "%nfile delete <args> OR|@",
subcommands = {FileCreate.class, FileDelete.class})
// This subcommand here is not for the real subcommand handling it is only for documentation
public class File implements Runnable {

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public void handle(String subCommand, String... args) {
        switch (subCommand) {
            case "create":
                System.out.println(Arrays.asList(args));
                if (args.length == 0) {
                    CommandLine.usage(new FileCreate(), System.out);
                } else {
                    new CommandLine(new FileCreate()).execute(args);
                }
                break;
            case "delete":
                System.out.println(Arrays.asList(args));
                if (args.length == 0) {
                    CommandLine.usage(new FileDelete(), System.out);
                } else {
                    new CommandLine(new FileDelete()).execute(args);
                }
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
