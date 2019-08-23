package com.hedera.cli.hedera.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedera.cli.models.AddressBook;
import com.hedera.cli.models.Network;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DataDirectory {

  private String userHome = System.getProperty("user.home");
  private String directoryName = ".hedera";
  private String defaultNetworkName = "aspen";

  // Example usage:
  // String currentNetwork = DataDirectory.readFile("network.txt", "aspen");
  // String pathToSubDir = currentNetwork + File.separator + "accounts"
  public boolean mkHederaSubDir(String pathToSubDir) {
    Path subdirpath = Paths.get(pathToSubDir);
    Path path = Paths.get(userHome, directoryName, subdirpath.toString());
    File directory = new File(path.toString());
    return directory.mkdirs();
  }

  public void listNetworks(InputStream addressBookInputStream) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      AddressBook addressBook = mapper.readValue(addressBookInputStream, AddressBook.class);
      List<Network> networks = addressBook.getNetworks();
      DataDirectory dataDirectory = new DataDirectory();
      for (Network network: networks) {
        String currentNetwork = dataDirectory.readFile("network.txt", defaultNetworkName);
        if (currentNetwork != null) {
          if (currentNetwork.equals(network.getName())) {
            System.out.println("* " + network.getName());
          } else {
            System.out.println("  " + network.getName());
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String networkGetName(InputStream addressBookInputStream) {
    ObjectMapper objectMapper = new ObjectMapper();
    String nodeName = "";
    try {
      AddressBook addressBook = objectMapper.readValue(addressBookInputStream, AddressBook.class);
      List<Network> networks = addressBook.getNetworks();
      String currentNetwork = this.readFile("network.txt", defaultNetworkName);
      if (currentNetwork != null) {
        for (Network network : networks) {
          if (currentNetwork.equals(network.getName())) {
            nodeName = network.getName();
            return nodeName;
          }
        }
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return nodeName;
  }

  // pathToFile instead of fileName
  public void writeFile(String pathToFile, String value) {
    Path path = Paths.get(userHome, directoryName);
    boolean directoryExists = Files.exists(path);
    if (!directoryExists) {
      File directory = new File(path.toString());
      directory.mkdir();
    }
    // write the data
    Path filePath = Paths.get(userHome, directoryName, pathToFile);
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

  public String readFile(String pathToFile) {
    String value = null;
    Path path = Paths.get(userHome, directoryName);
    boolean directoryExists = Files.exists(path);
    if (!directoryExists) {
      File directory = new File(path.toString());
      directory.mkdir();
    }
    Path filePath = Paths.get(userHome, directoryName, pathToFile);

    BufferedReader br = null;
    try {
      File file = new File(filePath.toString());
      FileReader fr = new FileReader(file.getAbsolutePath());
      br = new BufferedReader(fr);
      value = br.readLine();
    } catch (IOException e) {
      // e.printStackTrace();
      return value;
    } finally {
      try {
        br.close();
      } catch (IOException e) {
        // System.err.println("An IOException was caught!");
        // e.printStackTrace();
        return value;
      }
    }

    return value;
  }

  public String readFile(String pathToFile, String defaultValue) {
    Path path = Paths.get(userHome, directoryName);
    boolean directoryExists = Files.exists(path);
    if (!directoryExists) {
      File directory = new File(path.toString());
      directory.mkdir();
    }

    // read the data from file
    Path filePath = Paths.get(userHome, directoryName, pathToFile);
    File file = new File(filePath.toString());
    boolean fileExists = Files.exists(filePath);
    if (!fileExists) {
      writeFile(pathToFile, defaultValue);
      return defaultValue;
    }

    try {
      FileReader fr = new FileReader(file.getAbsoluteFile());
      BufferedReader br = new BufferedReader(fr);
      defaultValue = br.readLine();
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(-1);
    }
    return defaultValue;
  }

  public HashMap<String, String> readFileHashmap(String pathToFile, HashMap<String, String> defaultValue) {
      // check if index.txt exists, if not, create one
      Path filePath = Paths.get(userHome, directoryName, pathToFile);
      File file = new File(filePath.toString());
      boolean fileExists = Files.exists(filePath);
      if (!fileExists) {
        // file does not exist so create a new file and write value
        writeFileHashmap(pathToFile, defaultValue);
        return defaultValue;
      }
      try {
        // file exist
        Scanner reader = new Scanner(file);
        HashMap<String, String> newHashmap = new HashMap<>();
        while (reader.hasNext()) {
          String line = reader.nextLine();
          String sliceLine = line.substring(1, line.length()-1);
          String[] splitLines = sliceLine.split(", ");
          for (int i = 0; i< splitLines.length; i++) {
            String[] keyValuePairs = splitLines[i].split("=");
            newHashmap.put(keyValuePairs[0], keyValuePairs[1]);
          }
        }
        return newHashmap;
      } catch (Exception e ) {
        e.printStackTrace();
      }
      return defaultValue;
  }

  public HashMap jsonToHashmap(String pathToFile) {
    Path filePath = Paths.get(userHome, directoryName, pathToFile);
    File file = new File(filePath.toString());
    HashMap newHashmap = new HashMap<>();
    ObjectMapper mapper = new ObjectMapper();
    try {
      String json = new Scanner(file).useDelimiter("\\Z").next();
      newHashmap = mapper.readValue(json, HashMap.class);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return newHashmap;
  }

  public HashMap<String, String> writeFileHashmap(String pathToFile, HashMap<String, String> defaultValue) {

//    try {
//      FileWriter fw = new FileWriter(pathToFile);
//      BufferedWriter bw = new BufferedWriter(fw);
//      HashMap<String, String> mHashmap = new HashMap<>();
//      mHashmap.put(defaultValue);
//      bw.write(mHashmap.toString());
//      bw.close();
//
      Path filePath = Paths.get(userHome, directoryName, pathToFile);
      File file = new File(filePath.toString());
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
    return defaultValue;
  }


  public void listFiles(String pathToSubDir) {
    String userHome = System.getProperty("user.home");
    String directoryName = ".hedera";
    Path subdirpath = Paths.get(pathToSubDir);
    Path path = Paths.get(userHome, directoryName, subdirpath.toString());

    try {
      Stream<Path> walk = Files.walk(path);
      List<String> result = walk.map(x -> x.toString())
              .filter(f -> f.endsWith(".json")).collect(Collectors.toList());
      if (result.isEmpty()) {
        System.out.println("No Hedera accounts have created in the current network");
      }
      result.forEach(System.out::println);
      walk.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getUserHome() {
    return userHome;
  }

  public String getDirectoryName() {
    return directoryName;
  }

  public String getDefaultNetworkName() {
    return defaultNetworkName;
  }

}