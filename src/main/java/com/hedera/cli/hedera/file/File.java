package com.hedera.cli.hedera.file;

import java.util.Arrays;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.crypto.PicocliSpringFactory;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private FileCreate fileCreate;

    @Autowired
    private FileDelete fileDelete;

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public void handle(String subCommand, String... args) {
        switch (subCommand) {
            case "create":
                System.out.println(Arrays.asList(args));
                if (args.length == 0) {
                    CommandLine.usage(fileCreate, System.out);
                } else {
                    try {
                        new CommandLine(fileCreate).execute(args);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "delete":
                System.out.println(Arrays.asList(args));
                if (args.length == 0) {
                    CommandLine.usage(fileDelete, System.out);
                } else {
                    try {
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
