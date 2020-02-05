package com.hedera.cli.config;

import com.hedera.cli.shell.ProgressBar;
import com.hedera.cli.shell.ProgressCounter;
import com.hedera.cli.shell.ShellHelper;

import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@Conditional(InteractiveModeCondition.class)
public class InteractiveConfig {

    @Bean
    public ShellHelper shellHelper(@Lazy Terminal terminal) {
        return new ShellHelper(terminal);
    }

    @Bean
    public InputReader inputReader(@Lazy LineReader lineReader) {
        return new InputReader(lineReader);
    }

    @Bean
    public ProgressBar progressBar(ShellHelper shellHelper) {
        return new ProgressBar(shellHelper);
    }

    @Bean
    public ProgressCounter progressCounter(@Lazy Terminal terminal) {
        return new ProgressCounter(terminal);
    }
}
