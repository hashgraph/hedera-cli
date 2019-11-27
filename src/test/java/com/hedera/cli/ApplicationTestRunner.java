package com.hedera.cli;

import org.jline.reader.ParsedLine;
import org.jline.reader.Parser;
import org.jline.reader.SyntaxError;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.jline.JLineShellAutoConfiguration;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@TestConfiguration
@EnableAutoConfiguration(exclude = { JLineShellAutoConfiguration.class })
public class ApplicationTestRunner implements ApplicationRunner {

  @Override
  public void run(ApplicationArguments args) throws Exception {
    System.out.println("ApplicationTestRunner runs");
  }

  @Bean
  public Parser parser() {
    return new Parser() {
      public ParsedLine parse(String var1, int v2, Parser.ParseContext var3) throws SyntaxError {
        return null;
      }
    };
  }
}