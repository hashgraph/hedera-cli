package com.hedera.cli;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.cli.models.AddressBook;
import com.hedera.cli.models.Network;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class HederaNetwork {

  @Autowired
  ShellHelper shellHelper;
  
  @ShellMethod(value = "switch and manage different Hedera network")
  public void network() {
    System.out.println("Stub function.");
  }

  @ShellMethod(value = "list available Hedera network")
  public void list() {
       InputStream addressBookInputStream = getClass().getClassLoader().getResourceAsStream("/addressbook.json");
       ObjectMapper objectMapper = new ObjectMapper();
       try {
          AddressBook addressBook = objectMapper.readValue(addressBookInputStream, AddressBook.class);          
          List<Network> networks = addressBook.getNetworks();
          for (Network network: networks) {
            System.out.println(network.getName());
          }
       } catch (IOException e) {
         e.printStackTrace();
       }
       
  }
}