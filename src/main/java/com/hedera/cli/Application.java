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
public class Application {
    public static void main(String[] args) {
        // using args, determine banner mode (LOG or OFF) and web application type (NONE or SERVLET)
        Banner.Mode bannerMode = determineBannerMode(args);
        WebApplicationType webApplicationType = determineWebApplicationType(args);       
        
        // Set the banner mode and application type types to our app
        SpringApplication app = new SpringApplicationBuilder(Application.class)
            .bannerMode(bannerMode)
            .web(webApplicationType)
            .build();
        app.run(args);
    }

    private static Banner.Mode determineBannerMode(String[] args) {
        NonREPLHelper.putCache("X", "true");
        Banner.Mode bannerMode = Banner.Mode.CONSOLE;
        for (String arg: args) {
            boolean S = "-S".equals(arg);
            boolean X = "-X".equals(arg);
            if (X || S) {
                NonREPLHelper.putCache("X", "false");
                bannerMode = Banner.Mode.OFF;
            }
        }
        return bannerMode;
    }

    private static WebApplicationType determineWebApplicationType(String[] args) {
        WebApplicationType webEnvironment = WebApplicationType.NONE;    
        for (String arg: args) {
            boolean S = "-S".equals(arg);
            if (S) {
                webEnvironment = WebApplicationType.SERVLET;
            }
        }
        return webEnvironment;
    }
}