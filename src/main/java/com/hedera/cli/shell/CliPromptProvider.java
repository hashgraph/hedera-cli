package com.hedera.cli.shell;

import java.io.File;

import com.hedera.cli.hedera.utils.DataDirectory;
import com.hedera.cli.services.CurrentAccountService;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;

@Component
public class CliPromptProvider implements PromptProvider {

    @Autowired
    ApplicationContext context;

    private String defaultNetworkName = "aspen";

    @Override
    public AttributedString getPrompt() {

        System.out.println("Check on our context");
        System.out.println(context);

        DataDirectory dataDirectory = new DataDirectory();
        String currentNetwork = dataDirectory.readFile("network.txt", defaultNetworkName);
        String pathToDefaultAccount = currentNetwork + File.separator + "accounts" + File.separator + "default.txt";
        String defaultAccount = "";
        try {
            defaultAccount = dataDirectory.readFile(pathToDefaultAccount);
        } catch (Exception e) {
            // do nothing
        }

        // red
        AttributedString noDefaultAccountAttr = new AttributedString(
                "You do not have a default operator account for this network. Please run `setup`\n"
                        + "To see available networks, enter `network`\n",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));

        // blue
        AttributedString hederaAttr = new AttributedString("hedera ",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE));

        // green
        AttributedString currentNetworkAttr = new AttributedString("[" + currentNetwork + "]",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN));

        // blue
        AttributedString promptAttr = new AttributedString(" :> ",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE));

        // one more: current operator account

        // builder returns different AttributedString depending on whether default
        // operator account for this
        // network has been set or not
        AttributedStringBuilder builder = new AttributedStringBuilder();

        if (defaultAccount.isEmpty()) {
            return builder.append(noDefaultAccountAttr).append(hederaAttr).append(currentNetworkAttr).append(promptAttr)
                    .toAttributedString();
        }

        // yellow
        String currAccount = defaultAccount.split(":")[1];
        CurrentAccountService currentAccountService = context.getBean("currentAccount", CurrentAccountService.class);
        String accountNumber = currentAccountService.getAccountNumber();
        if (!StringUtil.isNullOrEmpty(accountNumber)) {
            System.out.println("We have an in-memory current account");
            currAccount = accountNumber;
        } 
        AttributedString currentAccountAttr = new AttributedString("[" + currAccount + "]",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));

        return builder.append(hederaAttr).append(currentNetworkAttr).append(currentAccountAttr).append(promptAttr)
                .toAttributedString();
    }
}
