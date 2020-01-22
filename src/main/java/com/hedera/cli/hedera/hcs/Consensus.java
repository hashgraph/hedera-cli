package com.hedera.cli.hedera.hcs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Component
@Command(name = "hcs", description = "@|fg(225) Create, update, delete or querying an account by providing the <args>|@"
+ "%n@|fg(yellow) <command> <subcommand> <args>" + "%neg. account create <args>|@", subcommands = {
    CreateTopic.class
})
public class Consensus implements Runnable {

    @Autowired
    private CreateTopic createTopic;

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }
 
    public void handle(String subCommand, String... args) {
        if (args.length == 0) {
            CommandLine.usage(this, System.out);
        } else {
            try {
                createTopic.handle(subCommand, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}