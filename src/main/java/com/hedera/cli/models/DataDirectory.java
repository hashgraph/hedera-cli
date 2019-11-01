package com.hedera.cli.models;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedera.cli.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class DataDirectory {

    @Autowired
    private ShellHelper shellHelper;

    private Path dataDir;

    @PostConstruct
    public void init() {
        // our default directories that is used
        String userHome = System.getProperty("user.home");
        String directoryName = ".hedera";
        this.dataDir = Paths.get(userHome, directoryName);
    }

    private void mkDataDir() {
        boolean directoryExists = Files.exists(dataDir);
        if (!directoryExists) {
            File directory = new File(dataDir.toString());
            directory.mkdir();
        }
    }

    // Example usage:
    // String currentNetwork = DataDirectory.readFile("network.txt", "testnet");
    // String pathToSubDir = currentNetwork + File.separator + "accounts"
    public boolean mkHederaSubDir(String pathToSubDir) {
        Path subdirpath = Paths.get(pathToSubDir);
        Path path = Paths.get(dataDir.toString(), subdirpath.toString());
        File directory = new File(path.toString());
        return directory.mkdirs();
    }

    // pathToFile instead of fileName
    public void writeFile(String pathToFile, String value) {
        mkDataDir();
        // write the data
        Path filePath = Paths.get(dataDir.toString(), pathToFile);
        File file = new File(filePath.toString());
        try {
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(value);
            bw.close();
        } catch (IOException e) {
            shellHelper.printError(e.getMessage());
        }
    }

    public String readFile(String pathToFile) {
        String value = "";
        boolean directoryExists = Files.exists(dataDir);
        if (!directoryExists) {
            File directory = new File(dataDir.toString());
            directory.mkdir();
        }
        Path filePath = Paths.get(dataDir.toString(), pathToFile);
        File file = new File(filePath.toString());

        // file does not exist, return ""
        if (!file.exists()) {
            return value;
        }

        // file exists, read it
        BufferedReader br = null;
        try {
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
                shellHelper.printError(e.getMessage());
            }
        }
        return value;
    }

    public String readFile(String pathToFile, String defaultValue) {
        boolean directoryExists = Files.exists(dataDir);
        if (!directoryExists) {
            File directory = new File(dataDir.toString());
            directory.mkdir();
        }

        // read the data from file
        Path filePath = Paths.get(dataDir.toString(), pathToFile);
        File file = new File(filePath.toString());
        boolean fileExists = Files.exists(filePath);
        if (!fileExists) {
            writeFile(pathToFile, defaultValue);
            return defaultValue;
        }

        String resultValue = defaultValue;
        try {
            // file exist, check if empty
            FileReader fr = new FileReader(file.getAbsoluteFile());
            BufferedReader br = new BufferedReader(fr);
            resultValue = br.readLine();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultValue;
    }

    public HashMap<String, String> readWriteToIndex(String pathToFile, HashMap<String, String> defaultValue) {
        // check if index.txt exists, if not, create one
        Path filePath = Paths.get(dataDir.toString(), pathToFile);
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
        return updatedHashmap.toString().substring(1, updatedHashmap.toString().length() - 1).replace(", ", "\n");
    }

    public void readIndex(String pathToFile) {
        // check if index.txt exists, if not, create one
        Path filePath = Paths.get(dataDir.toString(), pathToFile);
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
        Path filePath = Paths.get(dataDir.toString(), pathToFile);
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
        Path filePath = Paths.get(dataDir.toString(), pathToFile);
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
        Path filePath = Paths.get(dataDir.toString(), pathToFile);
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
        Path subdirpath = Paths.get(pathToSubDir);
        Path path = Paths.get(dataDir.toString(), subdirpath.toString());

        try {
            Stream<Path> walk = Files.walk(path);
            List<String> result = walk.map(x -> x.toString()).filter(f -> f.endsWith(".json"))
                    .collect(Collectors.toList());
            if (result.isEmpty()) {
                System.out.println("No Hedera accounts have created in the current network");
            }
            result.forEach(System.out::println);
            walk.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}