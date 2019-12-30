/*
package com.hedera.cli.hedera.setup;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.shell.Input;
import org.springframework.shell.Shell;
import org.springframework.test.context.ActiveProfiles;

@Tag("integration")
@ActiveProfiles("integration")
@SpringBootTest
@ComponentScan("com.hedera.cli")
public class SetupIT {

  @Autowired
  private ApplicationContext context;

  @Autowired
  private Shell shell;

  @Test
  public void testSetup() {
    Setup setup = (Setup) context.getBean(Setup.class);
    assertNotNull(setup);
    // setup.run();

    shell.evaluate(new Input() {
      @Override
      public String rawText() {
        return "exit";
      }
    });

  }

}*/
