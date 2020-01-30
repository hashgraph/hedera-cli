package com.hedera.cli.shell;

import com.hedera.cli.hedera.Hedera;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public class CliPromptProvider implements PromptProvider {

    @Autowired
    private Hedera hedera;

    @Override
    public AttributedString getPrompt() {
        String currentNetwork = hedera.getAddressBookManager().getCurrentNetworkAsString();
        String operatorAccount = hedera.getOperatorAccount();

        AttributedString noDefaultAccountAttr = new AttributedString(
                "To see available networks, enter `network ls`" +
                "\nYou do not have a default operator account for this network. Please run `setup`\n",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));

        AttributedString hederaAttr = new AttributedString("hedera ",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE));

        AttributedString currentNetworkAttr = new AttributedString("[" + currentNetwork + "]",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN));

        AttributedString promptAttr = new AttributedString(" :> ",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE));

        // builder returns different AttributedString depending on whether
        // default operator account for this network has been set or not
        AttributedStringBuilder builder = new AttributedStringBuilder();

        if (operatorAccount.isEmpty()) { // @formatter:off
            return builder
                .append(noDefaultAccountAttr)
                .append(hederaAttr)
                .append(currentNetworkAttr)
                .append(promptAttr)
                .toAttributedString();
        } 
        
        AttributedString currentAccountAttr = new AttributedString("[" + operatorAccount + "]",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));

        return builder
                .append(hederaAttr)
                .append(currentNetworkAttr)
                .append(currentAccountAttr)
                .append(promptAttr)
                .toAttributedString();
    } // @formatter:on
}
