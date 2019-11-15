package com.hedera.cli.hedera.crypto;

import com.hedera.cli.config.InputReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Command(name = "transfer", description = "@|fg(225) Crypto transfer to single or multiple accounts|@"
        + "%n@|fg(yellow) transfer -s 0.0.1001,0.0.1002 -r 0.0.1003,0.0.1004 -tb -1000,-1000,1000,1000|@"
        + "%ntransfer -s 0.0.1001,0.0.1002 -r 0.0.1003,0.0.1004 -hb -0.1,-100,1.20,100.999978|@")
public class Transfer implements Runnable {

    @Autowired
    private InputReader inputReader;

    // @Autowired
    // private CryptoTransfer cryptoTransfer;

    @Autowired
    private KryptoKransfer kryptoKransfer;

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public void handle(String... args) {
        System.out.println(this);
        System.out.println(kryptoKransfer);
        kryptoKransfer.setInputReader(inputReader);
        if (args.length == 0) {
            CommandLine.usage(kryptoKransfer, System.out);
        } else {
            try {
                System.out.println(args);
                new CommandLine(kryptoKransfer).execute(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
