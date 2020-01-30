package com.hedera.cli.services;

import lombok.Data;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Data
@Service
public class CurrentAccountService {

  private String network;
  private String accountNumber;
  private String privateKey;
  private String publicKey;

  @Bean("currentAccount")
  @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
  public CurrentAccountService getCurrentAccountService() {
    return new CurrentAccountService();
  }

}