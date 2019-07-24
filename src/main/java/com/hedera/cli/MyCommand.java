package com.hedera.cli;

import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;


@Component
@Command(name = "myCommand")
public class MyCommand implements Runnable {
    public static void main(String[] args) {
        new CommandLine(new MyCommand()).execute(args);
    }

    @Override
    public void run() {
        System.out.println("Hello World!");
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