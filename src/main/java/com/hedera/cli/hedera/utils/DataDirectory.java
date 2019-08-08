package com.hedera.cli.hedera.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedera.cli.models.AddressBook;
import com.hedera.cli.models.Network;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DataDirectory {

  // e.g. "mainnet/accounts"
  static public void mkHederaSubDir(String pathToSubDir) {
    String userHome = System.getProperty("user.home");
    String directoryName = ".hedera";
    Path subdirpath = Paths.get(pathToSubDir);
    Path path = Paths.get(userHome, directoryName, subdirpath.toString());
    
    boolean directoryExists = Files.exists(path);
    if (!directoryExists) {
      File directory = new File(path.toString());
      directory.mkdirs();
    }
  }

  public void readJsonToMap(InputStream addressBookInputStream) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      AddressBook addressBook = objectMapper.readValue(addressBookInputStream, AddressBook.class);
      List<Network> networks = addressBook.getNetworks();
      for (Network network: networks) {
        String currentNetwork = DataDirectory.readFile("network.txt", "aspen");
        if (currentNetwork.equals(network.getName())) {
          System.out.println("* " + network.getName());
        } else {
          System.out.println("  " + network.getName());
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  static public void writeFile(String fileName, String value) {
    String userHome = System.getProperty("user.home");
    String directoryName = ".hedera";
    Path path = Paths.get(userHome, directoryName);
    boolean directoryExists = Files.exists(path);
    if (!directoryExists) {
      File directory = new File(path.toString());
      directory.mkdir();
    }

    // write the data
    Path filePath = Paths.get(userHome, directoryName, fileName);
    File file = new File(filePath.toString());
    try {
      FileWriter fw = new FileWriter(file.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(value);
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(-1);
    }
  }

  static public String readFile(String fileName, String defaultValue) {
    String currentNetwork = defaultValue;
    String userHome = System.getProperty("user.home");
    String directoryName = ".hedera";
    Path path = Paths.get(userHome, directoryName);
    boolean directoryExists = Files.exists(path);
    if (!directoryExists) {
      File directory = new File(path.toString());
      directory.mkdir();
    }

    // read the data from file
    Path filePath = Paths.get(userHome, directoryName, fileName);
    File file = new File(filePath.toString());
    boolean fileExists = Files.exists(filePath);
    if (!fileExists) {
      writeFile(fileName, currentNetwork);
      return currentNetwork;
    }

    try {
      FileReader fr = new FileReader(file.getAbsoluteFile());
      BufferedReader br = new BufferedReader(fr);
      currentNetwork = br.readLine();
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(-1);
    }
    
    return currentNetwork;
  }
  
}