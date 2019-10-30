package com.hedera.cli.hedera.crypto;

import com.hedera.cli.config.InputReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.Arrays;

@Component
@Command(name = "transfer", description = "@|fg(225) Crypto transfer to single or multiple accounts|@"
        + "%n@|fg(yellow) transfer -s 0.0.1001,0.0.1002 -r 0.0.1003,0.0.1004 -tb -1000,-1000,1000,1000|@"
        + "%ntransfer -s 0.0.1001,0.0.1002 -r 0.0.1003,0.0.1004 -hb -0.1,-100,1.20,100.999978|@")
public class Transfer implements Runnable {

    @Autowired
    private Crypto crypto;

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public void handle(InputReader inputReader, String... args) {

        crypto.setInputReader(inputReader);
        if (args.length == 0) {
            CommandLine.usage(crypto, System.out);
        } else {
            try {
                System.out.println("or came in here");
                System.out.println(Arrays.asList(args));
                new CommandLine(crypto).execute(args);
            } catch (Exception e) {
                System.out.println("threw out here?");
                e.printStackTrace();
            }
        }
    }
}
