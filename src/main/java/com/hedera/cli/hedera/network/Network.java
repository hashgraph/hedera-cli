package com.hedera.cli.hedera.network;

import com.hedera.cli.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Component
@Command(name = "network", description = "List and set the network in use", subcommands = { NetworkList.class,
        NetworkUse.class })
public class Network implements Runnable {

    @Autowired
    private NetworkList networkList;

    @Autowired
    private NetworkUse networkUse;

    @Autowired
    private ShellHelper shellHelper;

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public void handle(String subCommand, String... args) {
        switch (subCommand) {
        case "ls":
            try {
                if (args.length == 0) {
                    new CommandLine(networkList).execute(args);
                } else {
                    CommandLine.usage(networkList, System.out);
                }
            } catch (Exception e) {
                shellHelper.printError(e.getMessage());
            }
            break;
        case "use":
            if (args.length == 0) {
                CommandLine.usage(new NetworkUse(), System.out);
            } else {
                try {
                    new CommandLine(networkUse).execute(args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
        default:
            this.run();
            break;
        }
    }

}