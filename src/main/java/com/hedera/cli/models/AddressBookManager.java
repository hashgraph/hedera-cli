package com.hedera.cli.models;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedera.cli.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class AddressBookManager {

  private List<Network> networks;

  private List<MirrorNode> mirrorNodes;

  @Autowired
  private DataDirectory dataDirectory;

  @Autowired
  private ShellHelper shellHelper;

  static private String MIRRORNODE_DEFAULT = "mirrornode.json";
  static private String ADDRESSBOOK_DEFAULT = "addressbook.json";
  static private final String NETWORK_DEFAULT = "testnet";
  static private final String NETWORK_FILE = "network.txt";
  static public final String ACCOUNT_DEFAULT_FILE = "default.txt";

  @PostConstruct
  public void init() {
    prepareNetworks();
    prepareMirrorNodes();

    // ensure that all sub-directories are created
    List<String> networkList = getNetworksAsStrings();
    for (String network : networkList) {
      String accountsDirForNetwork = network + File.separator + "accounts";
      dataDirectory.mkHederaSubDir(accountsDirForNetwork);
    }
  }

  private void prepareMirrorNodes() {
    String mirrorNodesJsonPath = File.separator + MIRRORNODE_DEFAULT;
    ObjectMapper mapper = new ObjectMapper();
    InputStream input = getClass().getResourceAsStream(mirrorNodesJsonPath);
    try {
      AddressBookMirror addressBookMirror = mapper.readValue(input, AddressBookMirror.class);
      mirrorNodes = addressBookMirror.getMirrorNodes();
    } catch (Exception e) {
      shellHelper.printError(e.getMessage());
    }
  }

  private void prepareNetworks() {
    // read in addressbook.json
    String addressBookJsonPath = File.separator + ADDRESSBOOK_DEFAULT;
    ObjectMapper mapper = new ObjectMapper();
    InputStream input = getClass().getResourceAsStream(addressBookJsonPath);
    try {
      AddressBook addressBook = mapper.readValue(input, AddressBook.class);

      // check to see if there is an additional addressbook.json in ~/.hedera
      // directory
      // if it exists, we will deep merge our /resources/addressbook.json with
      // ~/.hedera/addressbook.json
      Path additionalAddressBook = Paths.get(dataDirectory.getDataDir().toString(), "addressbook.json");
      File additionalAddressBookFile = new File(additionalAddressBook.toString());
      if (additionalAddressBookFile.exists()) {
        InputStream additionalInput = new FileInputStream(additionalAddressBookFile);
        addressBook = mapper.readerForUpdating(addressBook).readValue(additionalInput);
      }

      setNetworks(addressBook.getNetworks());
    } catch (Exception e) {
      shellHelper.printError(e.getMessage());
    }
  }

  public List<String> getNetworksAsStrings() {
    List<String> list = new ArrayList<String>();
    if (networks != null) {
      for (Network network : networks) {
        list.add(network.getName());
      }
    }
    return list;
  }

  public String getCurrentNetworkAsString() {
    return dataDirectory.readFile(NETWORK_FILE, NETWORK_DEFAULT);
  }

  public Network getCurrentNetwork() {
    String currentNetworkString = getCurrentNetworkAsString();
    if (networks != null) {
      for (Network network : networks) {
        if (network.getName().equals(currentNetworkString)) {
          return network;
        }
      }
    }
    return null;
  }

  public void listNetworks() {
    String currentNetwork = dataDirectory.readFile(NETWORK_FILE, NETWORK_DEFAULT);
    for (Network network : networks) {
      if (currentNetwork.equals(network.getName())) {
        System.out.println("* " + network.getName());
      } else {
        System.out.println("  " + network.getName());
      }
    }
  }

  public void listMirrorNodes() {
    String currentNetwork = dataDirectory.readFile(NETWORK_FILE, NETWORK_DEFAULT);
    for (MirrorNode m : mirrorNodes) {
      String currentMirror = currentNetwork + "-mirror";
      if (currentMirror.equals(m.getName())) {
        System.out.println("* " + m.getName());
      } else {
        System.out.println("  " + m.getName());
      }
    }
  }

}