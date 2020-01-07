package com.hedera.cli;

import com.hedera.cli.services.ExecutionService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan("com.hedera.cli")
public class Application {
    public static void main(String[] args) {
        // by default, cli executes in interactive mode (mode = true)
        ExecutionService.putCache("X", "true");
        for (String arg: args) {
            // if user specifies -X, we will set cli execution to non-interactive mode (mode = false)
            if (arg.equals("-X")) {
                ExecutionService.putCache("X", "false");
            }
        }
 
        SpringApplicationBuilder appBuilder = new SpringApplicationBuilder(Application.class);
        SpringApplication app = appBuilder.build();
        app.run(args);
    }
}