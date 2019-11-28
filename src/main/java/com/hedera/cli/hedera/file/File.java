package com.hedera.cli.hedera.file;

import javax.annotation.PostConstruct;

import com.hedera.cli.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.Arrays;

@Component
@Command(name = "file", description = "@|fg(225) Create, update, delete file.|@"
        + "%n@|fg(yellow) file create <args> OR" + "%nfile update <args> OR"
        + "%nfile delete <args> OR|@", subcommands = {FileCreate.class, FileDelete.class,
        FileInfo.class})
// This subcommand here is not for the real subcommand handling it is only for
// documentation
public class File implements Runnable {

    @Autowired
    private FileCreate fileCreate;

    @Autowired
    private FileDelete fileDelete;

    @Autowired
    private FileInfo fileInfo;

    @Autowired
    private ShellHelper shellHelper;

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public void handle(String subCommand, String... args) {
        switch (subCommand) {
            case "create":
                if (args.length == 0) {
                    CommandLine.usage(fileCreate, System.out);
                } else {
                    CommandLine commandLine = new CommandLine(fileCreate);
                    commandLine.execute(args);
                }
                break;
            case "delete":
                if (args.length == 0) {
                    CommandLine.usage(fileDelete, System.out);
                } else {
                    CommandLine commandLine = new CommandLine(fileDelete);
                    commandLine.execute(args);
                }
                break;
            case "info":
                if (args.length == 0) {
                    CommandLine.usage(fileInfo, System.out);
                } else {
                    CommandLine commandLine = new CommandLine(fileInfo);
                    commandLine.execute(args);
                }
                break;
            case "update":
                shellHelper.printInfo("Not yet implemented");
                break;
            default:
                this.run();
                break;
        }
    }
}
