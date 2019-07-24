package com.hedera.cli.config;

import com.hedera.cli.shell.ProgressBar;
import com.hedera.cli.shell.ProgressCounter;
import com.hedera.cli.shell.ShellHelper;
import org.jline.terminal.Terminal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import picocli.CommandLine.IFactory;

@Configuration
public class SpringShellConfig {

    @Bean
    public ShellHelper shellHelper(@Lazy Terminal terminal) {
        return new ShellHelper(terminal);
    }

    @Bean
    public IFactory iFactory() {
        return new IFactory() {
            @Override
            public <K> K create(Class<K> cls) throws Exception {
                return null;
            }
        };
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
