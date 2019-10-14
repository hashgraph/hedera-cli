package com.hedera.cli.services;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
public class CurrentAccountService {

  private String accountNumber;
  private String privateKey;
  private String publicKey;

  @Bean("currentAccount")
  @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
  public CurrentAccountService getCurrentAccountService() {
    return new CurrentAccountService();
  }

  public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }

  public String getAccountNumber() {
    return this.accountNumber;
  }

  public String getPrivateKey() {
    return privateKey;
  }

  public void setPrivateKey(String privateKey) {
    this.privateKey = privateKey;
  }

  public String getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(String publicKey) {
    this.publicKey = publicKey;
  }
}