package com.hedera.cli.hedera.hcs;

import com.hedera.cli.config.InputReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Component
@Command(name = "hcs", description = "@|fg(225) Create, update, delete or querying an account by providing the <args>|@"
+ "%n@|fg(yellow) <command> <subcommand> <args>" + "%neg. account create <args>|@", subcommands = {
    CreateTopic.class, SubmitMessage.class, ReadMessage.class
})
public class Consensus implements Runnable {

    @Autowired
    private CreateTopic createTopic;

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }
 
    public void handle(String subCommand, String... args) {
        switch (subCommand) {
            case "create":
                if (args.length == 0) {
                    CommandLine.usage(this, System.out);
                } else {
                    createTopic.handle(subCommand, args);
                }
                break;
            case "submit":
                if (args.length == 0) {
                    CommandLine.usage(this, System.out);
                } else {
                    System.out.println("to be impl");
                    // submitMessage.handle(subCommand, args);
                }
                break;
            case "read":
                if (args.length == 0) {
                    CommandLine.usage(this, System.out);
                } else {
                    System.out.println("to be impl");
                    // readMessage.handle(subCommand, args);
                }
                break;
            default:
                this.run();
                break;
        }
    }
}