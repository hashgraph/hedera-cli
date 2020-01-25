package com.hedera.cli.hedera.hcs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Component
@Command(name = "hcs", description = "@|fg(225) Create a topic, submit a message or read a message in a topic|@"
        + "%n@|fg(yellow) <command> <subcommand> <args>"
        + "%neg. hcs create|@", subcommands = { CreateTopic.class })
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
            try {
                createTopic.handle(subCommand, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
            break;
        case "submit":
            break;
        default:
            this.run();
            break;
        }
    }
}