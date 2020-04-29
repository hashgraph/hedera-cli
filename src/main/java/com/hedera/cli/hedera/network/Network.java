package com.hedera.cli.hedera.network;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Component
@Command(name = "network", description = "List and set the network in use",
        subcommands = { NetworkList.class, NetworkUse.class })
public class Network implements Runnable {

    @Autowired
    private NetworkList networkList;

    @Autowired
    private NetworkUse networkUse;

    @Setter
    private CommandLine networkListCmd;

    @Setter
    private CommandLine networkUseCmd;

    @PostConstruct
    public void init() {
        this.networkListCmd = new CommandLine(networkList);
        this.networkUseCmd = new CommandLine(networkUse);
    }

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public void handle(String subCommand, String... args) {
        switch (subCommand) {
        case "ls":
            networkListCmd.execute(args);
            break;
        case "use":
            if (args.length == 0) {
                CommandLine.usage(networkUse, System.out);
            } else {
                networkUseCmd.execute(args);
            }
            break;
        default:
            this.run();
            break;
        }
    }

}