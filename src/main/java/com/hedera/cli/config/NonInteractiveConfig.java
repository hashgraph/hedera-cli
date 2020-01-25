package com.hedera.cli.config;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.hedera.cli.models.AddressBookManager;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;
import com.hedera.hashgraph.sdk.mirror.MirrorClient;
import com.hedera.hashgraph.sdk.mirror.MirrorConsensusTopicQuery;

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
    public CommandLineRunner nonREPLCommandLineRunner(ConfigurableEnvironment environment) {
        return new NonREPLCommandLineRunner(shell, environment);
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

    @Autowired
    private AddressBookManager am;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<String> nonOptionArgs = args.getNonOptionArgs();
        if (nonOptionArgs.get(0).equals("-X")) {
            // when -X option is provided, do not run local server
            return;
        }

        final MirrorClient mirrorClient = new MirrorClient(am.getCurrentMirrorNetwork());

        String topicIdString = nonOptionArgs.get(1);    // handle it if get(1) fails
        final ConsensusTopicId topicId = ConsensusTopicId.fromString(topicIdString);
        System.out.println("Subscribing to topic id " + topicIdString);
        // local server mode
        new MirrorConsensusTopicQuery()
            .setTopicId(topicId)
            .subscribe(mirrorClient, resp -> {
                String messageAsString = new String(resp.message, StandardCharsets.UTF_8);
                System.out.println(resp.consensusTimestamp + " received topic message: " + messageAsString);
            },Throwable::printStackTrace);
              
    }
}

@Order(InteractiveShellApplicationRunner.PRECEDENCE - 2)
class NonREPLCommandLineRunner implements CommandLineRunner {

    private Shell shell;

    private ConfigurableEnvironment environment;

    public NonREPLCommandLineRunner(Shell shell, ConfigurableEnvironment environment) {
        this.shell = shell;
        this.environment = environment;
    }

    @Override
    public void run(String... args) throws Exception {
        List<String> allArgs = new LinkedList<String>(Arrays.asList(args));
        String flag = allArgs.get(0);
        List<String> commandsToRun = Arrays.asList("quit");

        // flag can be -S or -X
        if ("-X".equals(flag)) {
            allArgs.remove(0);
            String[] commands = allArgs.toArray(new String[0]);
            commandsToRun = Arrays.stream(commands).filter(w -> !w.startsWith("@")).collect(Collectors.toList());            
        } 

        // either run quit (-S) or actually execute the commands (-X)
        InteractiveShellApplicationRunner.disable(environment);
        shell.run(new StringInputProvider(commandsToRun));
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