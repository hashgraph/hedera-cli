package com.hedera.cli.shell;

import com.hedera.cli.hedera.utils.DataDirectory;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public class CliPromptProvider implements PromptProvider {

    @Override
    public AttributedString getPrompt() {
        // blue
        AttributedString hederaAttr = new AttributedString("hedera ",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE));

        // green
        DataDirectory dataDirectory = new DataDirectory();
        String currentNetwork = dataDirectory.readFile("network.txt", "aspen");
        AttributedString currentNetworkAttr = new AttributedString("[" + currentNetwork + "]",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN));

        // blue
        AttributedString promptAttr = new AttributedString(" :> ",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE));

        // one more: current operator account

        AttributedStringBuilder builder = new AttributedStringBuilder();
        return builder.append(hederaAttr).append(currentNetworkAttr).append(promptAttr).toAttributedString();
    }
}
