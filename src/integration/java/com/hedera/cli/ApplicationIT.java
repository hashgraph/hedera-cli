/*
package com.hedera.cli;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

@Tag("integration")
@ActiveProfiles("integration")
@SpringBootTest
@ComponentScan("com.hedera.cli")
public class ApplicationIT {

  @Autowired
  private ApplicationContext context;

  @Test
  public void contextLoads() {
    System.out.println("Integration Tests run");
    Application.main(new String[] {});
    assertNotNull(context);
  }

}*/
