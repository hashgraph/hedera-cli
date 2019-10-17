package com.hedera.cli.hedera.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedera.cli.models.AddressBook;
import com.hedera.cli.models.Network;

import org.springframework.stereotype.Component;

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
            for (Network network : networks) {
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
        } catch (IOException e) {
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
            // file exist, check if empty
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

    public HashMap<String, String> readWriteToIndex(String pathToFile, HashMap<String, String> defaultValue) {
        // check if index.txt exists, if not, create one
        Path filePath = Paths.get(userHome, directoryName, pathToFile);
        File file = new File(filePath.toString());
        boolean fileExists = Files.exists(filePath);
        if (!fileExists) {
            // file does not exist so create a new file and write value
            writeFile(pathToFile, formatMapToIndex(defaultValue));
            return defaultValue;
        }

        try {
            // file exist
            Scanner reader = new Scanner(file);
            // read the new value
            String key = "";
            String value = "";
            for (Map.Entry<String, String> entry : defaultValue.entrySet()) {
                key = entry.getKey();
                value = entry.getValue();
            }
            // creates a new map
            HashMap<String, String> updatedHashmap = new HashMap<>();
            while (reader.hasNext()) {
                // checks the old map
                String line = reader.nextLine();
                String[] splitLines = line.split(", ");
                for (int i = 0; i < splitLines.length; i++) {
                    String[] keyValuePairs = splitLines[i].split("=");
                    updatedHashmap.put(keyValuePairs[0], keyValuePairs[1]);
                }
            }
            // appends old map with new value
            updatedHashmap.put(key, value);
            // write to file
            writeFile(pathToFile, formatMapToIndex(updatedHashmap));
            reader.close();
            return updatedHashmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public String formatMapToIndex(Map<String, String> updatedHashmap) {
        return updatedHashmap.toString()
                .substring(1, updatedHashmap.toString().length() - 1)
                .replace(", ", "\n");
    }

    public void readIndex(String pathToFile) {
        // check if index.txt exists, if not, create one
        Path filePath = Paths.get(userHome, directoryName, pathToFile);
        File file = new File(filePath.toString());

        try {
            // file exist
            Scanner reader = new Scanner(file);
            while (reader.hasNext()) {
                // checks the old map
                String line = reader.nextLine();
                System.out.println(line);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> readIndexToHashmap(String pathToFile) {
        // check if index.txt exists, if not, create one
        Path filePath = Paths.get(userHome, directoryName, pathToFile);
        File file = new File(filePath.toString());
        HashMap<String, String> mHashmap = new HashMap<>();

        try {
            // file exist
            Scanner reader = new Scanner(file);
            while (reader.hasNext()) {
                // checks the old map
                String line = reader.nextLine();
                String[] splitLines = line.split("\n");
                for (int i = 0; i < splitLines.length; i++) {
                    String[] keyValuePairs = splitLines[i].split("=");
                    mHashmap.put(keyValuePairs[0], keyValuePairs[1]);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mHashmap;
    }

    public HashMap<String, String> readFileHashmap(String pathToFile) {
        // check if index.txt exists, if not, create one
        Path filePath = Paths.get(userHome, directoryName, pathToFile);
        File file = new File(filePath.toString());
        HashMap<String, String> mHashmap = new HashMap<>();

        try {
            // file exist
            Scanner reader = new Scanner(file);
            while (reader.hasNext()) {
                // checks the old map
                String line = reader.nextLine();
                String sliceLine = line.substring(1, line.length() - 1);
                String[] splitLines = sliceLine.split(", ");
                for (int i = 0; i < splitLines.length; i++) {
                    String[] keyValuePairs = splitLines[i].split("=");
                    mHashmap.put(keyValuePairs[0], keyValuePairs[1]);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mHashmap;
    }

    public HashMap<String, String> jsonToHashmap(String pathToFile) {
        Path filePath = Paths.get(userHome, directoryName, pathToFile);
        File file = new File(filePath.toString());
        HashMap<String, String> newHashmap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            Scanner reader = new Scanner(file);
            String json = reader.useDelimiter("\\Z").next();
            TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {
            };
            newHashmap = mapper.readValue(json, typeRef);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newHashmap;
    }

    public void listFiles(String pathToSubDir) {
        String userHome = System.getProperty("user.home");
        String directoryName = ".hedera";
        Path subdirpath = Paths.get(pathToSubDir);
        Path path = Paths.get(userHome, directoryName, subdirpath.toString());

        try {
            Stream<Path> walk = Files.walk(path);
            List<String> result = walk.map(x -> x.toString()).filter(f -> f.endsWith(".json")).collect(Collectors.toList());
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