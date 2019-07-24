package com.hedera.cli.shell;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public class CliPromptProvider implements PromptProvider {

    @Override
    public AttributedString getPrompt() {
        return new AttributedString("hedera :> ", AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE));
    }
}
