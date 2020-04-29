package com.hedera.cli.models;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedera.cli.shell.ShellHelper;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.NonNull;

@Data
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
    public void writeFile(@NonNull String pathToFile, String value) {
        mkDataDir();

        // if pathToFile includes a folder path
        String[] paths = pathToFile.split(File.separator);
        if (paths.length > 1) {
            String[] folderPaths = Arrays.copyOf(paths, paths.length - 1);
            String folderPath = String.join(File.separator, folderPaths);
            mkHederaSubDir(folderPath);
        }

        // write the data
        Path filePath = Paths.get(dataDir.toString(), pathToFile);
        File file = new File(filePath.toString());
        try {
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(value);
            bw.close();
        } catch (IOException e) {
            shellHelper.printError("Failed to save");
        }
    }

    private File checkFileExists(String pathToFile) {
        Path filePath = Paths.get(dataDir.toString(), pathToFile);
        File file = new File(filePath.toString());
        if (!file.exists()) {
            return null;
        }
        return file;
    }

    public String readFile(@NonNull String pathToFile) {
        mkDataDir();

        // defaults to return an empty string
        String value = "";

        // return empty string if file does not exist
        File file = checkFileExists(pathToFile);
        if (file == null)
            return value;

        BufferedReader br = null;
        try {
            FileReader fr = new FileReader(file.getAbsolutePath());
            br = new BufferedReader(fr);
            value = br.readLine();
            br.close();
        } catch (IOException e) {
            return value;
        }
        return value;
    }

    // attempts to read a file, if file does not exist, write the default value into
    // it and return default value
    public String readFile(@NonNull String pathToFile, String defaultValue) {
        String value = readFile(pathToFile);
        if (value.isEmpty()) {
            writeFile(pathToFile, defaultValue);
            return defaultValue;
        }
        return value;
    }

    // read out a HashMap from our index.txt file, which is structured in a
    // particular way
    private HashMap<String, String> readMap(File file) throws FileNotFoundException {
        HashMap<String, String> map = new HashMap<>();
        Scanner reader = new Scanner(file);
        while (reader.hasNext()) {
            // checks the old map
            String line = reader.nextLine();
            String[] splitLines = line.split(", ");
            for (int i = 0; i < splitLines.length; i++) {
                String[] keyValuePairs = splitLines[i].split("=");
                map.put(keyValuePairs[0], keyValuePairs[1]);
            }
        }
        reader.close();
        return map;
    }

    private HashMap<String, String> mergeMap(HashMap<String, String> finalMap, HashMap<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (finalMap.get(key) == null) {
                finalMap.put(key, value);
            }
        }
        return finalMap;
    }

    public HashMap<String, String> readWriteToIndex(String pathToFile, HashMap<String, String> map) {
        // check if index.txt exists, if not, create one with the entire map
        // which can comprise many key-value-pairs
        File file = checkFileExists(pathToFile);
        if (file == null) {
            writeFile(pathToFile, formatMapToIndex(map));
            return map;
        }

        // file already exists, so this is the append-more-key-value-pairs scenario
        HashMap<String, String> finalMap = new HashMap<>();
        try {
            finalMap = readMap(file);
            if (map.isEmpty()) {
                return finalMap;
            }

            // appends old map with more key-value-pairs if key does not already exist
            finalMap = mergeMap(finalMap, map);
            // write to file
            writeFile(pathToFile, formatMapToIndex(finalMap));
        } catch (Exception e) {
            return null;
        }
        return finalMap;
    }

    public String formatMapToIndex(Map<String, String> updatedHashmap) {
        return updatedHashmap.toString().substring(1, updatedHashmap.toString().length() - 1).replace(", ", "\n");
    }

    public void listIndex(String pathToFile) {
        // check if index.txt exists, if not, create one
        Path filePath = Paths.get(dataDir.toString(), pathToFile);
        File file = new File(filePath.toString());
        try {
            // file exist
            Scanner reader = new Scanner(file);
            while (reader.hasNext()) {
                String line = reader.nextLine();
                System.out.println(line);
            }
            reader.close();
        } catch (Exception e) {
            shellHelper.printError("Unable to read index");
        }
    }

    public HashMap<String, String> readIndexToHashmap(String pathToFile) {
        // check if index.txt exists, if not, create one
        Path filePath = Paths.get(dataDir.toString(), pathToFile);
        File file = new File(filePath.toString());
        HashMap<String, String> map = new HashMap<>();

        try {
            // file exist
            map = readMap(file);
        } catch (Exception e) {
            return null;
        }
        return map;
    }

    public HashMap<String, String> readJsonToHashmap(String pathToFile) {
        Path filePath = Paths.get(dataDir.toString(), pathToFile);
        File file = new File(filePath.toString());
        HashMap<String, String> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            Scanner reader = new Scanner(file);
            String json = reader.useDelimiter("\\Z").next();
            TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {
            };
            map = mapper.readValue(json, typeRef);
            reader.close();
        } catch (Exception e) {
            return null;
        }
        return map;
    }

}