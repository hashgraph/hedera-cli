package com.hedera.cli;

import com.hedera.cli.services.NonREPLExecution;

import org.springframework.boot.Banner;
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
        boolean bannerMode = true;
        NonREPLExecution.putCache("X", "true");
        for (String arg: args) {
            // if user specifies -X, we will set cli execution to non-interactive mode (mode = false)
            if ("-X".equals(arg)) {
                NonREPLExecution.putCache("X", "false");
                bannerMode = false;
            }
        }
 
        SpringApplicationBuilder appBuilder = new SpringApplicationBuilder(Application.class);
        SpringApplication app = appBuilder.build();
        if (!bannerMode) {
            app.setBannerMode(Banner.Mode.OFF);
        }
        app.run(args);
    }
}