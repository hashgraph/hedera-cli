package com.hedera.cli.hedera.mirror;

import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Component
@Command(name = "mirror", description = "List the mirror nodes")
public class Mirror implements Runnable {

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public void handle(String subCommand) {
        switch (subCommand) {
        case "ls":
            break;
        default:
            this.run();
            break;
        }
    }

}