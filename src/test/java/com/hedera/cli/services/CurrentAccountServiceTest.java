package com.hedera.cli.services;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { CurrentAccountService.class })
public class CurrentAccountServiceTest {

  @Autowired
  ApplicationContext context;

  @Test
  public void testSingleton() {
    // test value for our singleton
    String testAccountNumber = "0.0.1001";

    // retrieve our singleton and set our test value in it
    CurrentAccountService accountService = (CurrentAccountService) context.getBean("currentAccount", CurrentAccountService.class);
    accountService.setAccountNumber(testAccountNumber);

    // instantiate CurrentAccount service again and prove that it is indeed the singleton
    CurrentAccountService accountServiceAgain = context.getBean("currentAccount", CurrentAccountService.class);
    String testAccountNumberAgain = accountServiceAgain.getAccountNumber();
   
    assertEquals(testAccountNumber, testAccountNumberAgain);
  }

}