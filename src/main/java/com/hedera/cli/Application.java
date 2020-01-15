package com.hedera.cli;

import com.hedera.cli.services.NonREPLHelper;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
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
        WebApplicationType webEnvironment = WebApplicationType.NONE;
        NonREPLHelper.putCache("X", "true");
        for (String arg: args) {
            // if user specifies -X or -S, we will set cli execution to non-interactive mode (mode = false)
            if ("-X".equals(arg) || "-S".equals(arg)) {
                NonREPLHelper.putCache("X", "false");
                bannerMode = false;
            }
            if ("-S".equals(arg)) {
                webEnvironment = WebApplicationType.SERVLET;
            }
        }
 
        SpringApplicationBuilder appBuilder = new SpringApplicationBuilder(Application.class);
        SpringApplication app = appBuilder.build();
        if (!bannerMode) {
            app.setBannerMode(Banner.Mode.OFF);
        }
        app.setWebApplicationType(webEnvironment);
        app.run(args);
    }
}