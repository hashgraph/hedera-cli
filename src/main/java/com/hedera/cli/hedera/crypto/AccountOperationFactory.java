package com.hedera.cli.hedera.crypto;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountOperationFactory {

  @Autowired
  private AccountGetInfo accountGetInfo;

  @Autowired
  private AccountList accountList;

  @Autowired
  private AccountRecovery accountRecovery;

  @Autowired
  private AccountUse accountUse;

  @Autowired
  private AccountBalance accountBalance;

  @Autowired
  private AccountDelete accountDelete;

  @Autowired
  private AccountUpdate accountUpdate;

  @Autowired
  private AccountCreate accountCreate;

  @Autowired
  private AccountDefault accountDefault;

  private Map<String, Operation> operationMap = new HashMap<>();
  
  @PostConstruct
  public void init() {
    operationMap.put("info", accountGetInfo);
    operationMap.put("ls", accountList);
    operationMap.put("recovery", accountRecovery);
    operationMap.put("use", accountUse);
    operationMap.put("balance", accountBalance);
    operationMap.put("delete", accountDelete);
    operationMap.put("update", accountUpdate);
    operationMap.put("create", accountCreate);
    operationMap.put("default", accountDefault);
  }

  public Optional<Operation> getOperation(String operator) {
    return Optional.ofNullable(operationMap.get(operator));
  }
  
}