package com.hedera.cli.config;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.hedera.cli.shell.ShellHelper;

import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeExceptionMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.shell.ExitRequest;
import org.springframework.shell.Input;
import org.springframework.shell.InputProvider;
import org.springframework.shell.Shell;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.util.StringUtils;

@Configuration
@Conditional(NonInteractiveModeCondition.class)
public class NonInteractiveConfig {

    @Autowired
    private Shell shell;

    @Bean
    public CommandLineRunner exampleCommandLineRunner(ConfigurableEnvironment environment) {
        System.out.println(1);
        return new ExampleCommandLineRunner(shell, environment);
    }

    @Bean
    @Conditional(NonInteractiveModeCondition.class)
    public ApplicationRunner applicationRunner() {
        return new LocalServer();
    }

    @Bean
    public ExitCodeExceptionMapper exitCodeExceptionMapper() {
        return exception -> {
            Throwable e = exception;
            while (e != null && !(e instanceof ExitRequest)) {
                e = e.getCause();
            }
            return e == null ? 1 : ((ExitRequest) e).status();
        };
    }

    @Bean
    public ShellHelper shellhelper(@Lazy Terminal terminal) {
        return new ShellHelper(terminal);
    }

    @Bean
    public InputReader inputReader(@Lazy LineReader lineReader) {
        return new InputReader(lineReader);
    }
}

class LocalServer implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println(args.getNonOptionArgs());
        System.out.println("Running local server");
    }
}

@Order(InteractiveShellApplicationRunner.PRECEDENCE - 2)
class ExampleCommandLineRunner implements CommandLineRunner {

    private Shell shell;

    private ConfigurableEnvironment environment;

    public ExampleCommandLineRunner(Shell shell, ConfigurableEnvironment environment) {
        this.shell = shell;
        this.environment = environment;
    }

    @Override
    public void run(String... args) throws Exception {
        // remove the first element ("-X")
        // System.exit(0);
        List<String> allArgs = new LinkedList<String>(Arrays.asList(args));
        if (allArgs.get(0).equals("-S")) {
            List<String> commandsToRun = Arrays.asList("quit");
            // if (!commandsToRun.isEmpty()) {
                InteractiveShellApplicationRunner.disable(environment);
                shell.run(new StringInputProvider(commandsToRun));
        }
        // allArgs.remove(0);
        // String[] commands = allArgs.toArray(new String[0]);
        // List<String> commandsToRun = Arrays.stream(commands).filter(w -> !w.startsWith("@")).collect(Collectors.toList());
        // if (!commandsToRun.isEmpty()) {
            // InteractiveShellApplicationRunner.disable(environment);
            // shell.run(new StringInputProvider(commandsToRun));
        // }
        System.out.println("moving on to run local server");
    }

}

class StringInputProvider implements InputProvider {

    private final List<String> words;

    private boolean done;

    public StringInputProvider(List<String> words) {
        this.words = words;
    }

    @Override
    public Input readInput() {
        if (!done) {
            done = true;
            return new Input() {
                @Override
                public List<String> words() {
                    return words;
                }

                @Override
                public String rawText() {
                    return StringUtils.collectionToDelimitedString(words, " ");
                }
            };
        } else {
            return null;
        }
    }
}