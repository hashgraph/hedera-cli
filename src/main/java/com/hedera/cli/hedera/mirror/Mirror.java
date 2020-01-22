package com.hedera.cli.hedera.mirror;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Component
@Command(name = "mirror", description = "List the mirror nodes")
public class Mirror implements Runnable {

    @Autowired
    private MirrorList mirrorList;

    @Setter
    private CommandLine mirrorListCmd;

    @PostConstruct
    public void init() {
        this.mirrorListCmd = new CommandLine(mirrorList);
    }

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public void handle(String subCommand) {
        switch (subCommand) {
        case "ls":
            mirrorListCmd.execute();
            break;
        default:
            this.run();
            break;
        }
    }

}