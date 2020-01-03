package com.hedera.cli;

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
        SpringApplicationBuilder appBuilder = new SpringApplicationBuilder(Application.class);
    
        // for (String arg: args) {
        //     System.out.println(arg);
        //     if (arg.equals("-X")) {
        //         appBuilder.properties("X=true");
        //     }
        // }

        SpringApplication app = appBuilder.build();
        app.run(args);
    }
}