package com.hedera.cli.hedera.file;

import java.util.Arrays;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.crypto.PicocliSpringFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Component
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

    public void handle(ApplicationContext context, InputReader inputReader, String subCommand, String... args) {
        PicocliSpringFactory factory = new PicocliSpringFactory(context);

        switch (subCommand) {
            case "create":
                System.out.println(Arrays.asList(args));
                if (args.length == 0) {
                    CommandLine.usage(new FileCreate(), System.out);
                } else {
                    try {
                        FileCreate fileCreate = factory.create(FileCreate.class);
                        new CommandLine(fileCreate).execute(args);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "delete":
                System.out.println(Arrays.asList(args));
                if (args.length == 0) {
                    CommandLine.usage(new FileDelete(), System.out);
                } else {
                    try {
                        FileDelete fileDelete = factory.create(FileDelete.class);
                        new CommandLine(fileDelete).execute(args);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
