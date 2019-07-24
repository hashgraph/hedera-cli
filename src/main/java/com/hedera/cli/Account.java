package com.hedera.cli;

import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParseResult;

import com.hedera.cli.hedera.CryptoCreate;


@Component
@Command(name = "account", subcommands = CryptoCreate.class)
public class Account implements Runnable {
    public static void main(String[] args) {
        CommandLine commandLine = new CommandLine(new Account());
        commandLine.addSubcommand("create", new CryptoCreate());

        ParseResult parsed = commandLine.parseArgs(args);
        handleParseResult(parsed);
        // commandLine.parseWithHandler(new CommandLine.RunLast(), args);
    }

    private static void handleParseResult(ParseResult parsed) {
        assert parsed.subcommand() != null : "at least 1 command and 1 subcommand found";
    
        ParseResult sub = parsed.subcommand();
        assert parsed.commandSpec().userObject().getClass() == Account.class       : "main command";
        assert    sub.commandSpec().userObject().getClass() == CryptoCreate.class : "subcommand";
    
        // Git git = (Git) parsed.commandSpec().userObject();
        // assert git.gitDir.equals(new File("/home/rpopma/picocli"));
    
        // GitStatus gitstatus = (GitStatus) sub.commandSpec().userObject();
        // assert  gitstatus.shortFormat              : "git status -s"
        // assert  gitstatus.branchInfo               : "git status -b"
        // assert !gitstatus.showIgnored              : "git status --showIgnored not specified"
        // assert  gitstatus.mode == GitStatusMode.no : "git status -u=no"
    }

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }
}
//public class MyCommand implements Callable<Integer> {
//
////    @Autowired
////    private SomeService someService;
//
//    // Prevent "Unknown option" error when users use
//    // the Spring Boot parameter 'spring.config.location' to specify
//    // an alternative location for the application.properties file.
//    @Option(names = "--spring.config.location", hidden = true)
//    private String springConfigLocation;
//
//    @Option(names = { "-x", "--option" }, description = "example option")
//    private boolean flag;
//
//    public Integer call() throws Exception {
//        // business logic here
//        return 0;
//    }
//}