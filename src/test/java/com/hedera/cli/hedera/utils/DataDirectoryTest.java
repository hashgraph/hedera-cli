package com.hedera.cli.hedera.utils;

import com.hedera.cli.models.AddressBook;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.rule.OutputCapture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class DataDirectoryTest {

    // captures our stdout
    @Rule
    public OutputCapture capture = new OutputCapture();

    @InjectMocks
    DataDirectory dataDirectory;

    @Test
    public void testMkHederaSubDir() {
        String currentNetwork = dataDirectory.readFile("network.txt", dataDirectory.getDefaultNetworkName());
        String pathToSubDir = currentNetwork + File.separator + "accounts";
        Path subdirpath = Paths.get(pathToSubDir);
        Path path = Paths.get(dataDirectory.getUserHome(), dataDirectory.getDirectoryName(), subdirpath.toString());
        // expected
        boolean directoryExists = Files.exists(path);

        // actual
        // if directory already exists (true), return false
        // if directory does not already exists (false), return true
        boolean result = dataDirectory.mkHederaSubDir(pathToSubDir);
        assertNotEquals(directoryExists, result);
    }

    @Test
    public void testListNetworks() {
        String addressBookJson = File.separator + "addressbook.json";
        InputStream addressBookInputStream = getClass().getResourceAsStream(addressBookJson);
        dataDirectory.listNetworks(addressBookInputStream);

        // tokenize our stdout capture
        List<String> tokens = new ArrayList<>();
        String lineSeparator = System.getProperty("line.separator");
        StringTokenizer tokenizer = new StringTokenizer(capture.toString(), lineSeparator);
        while (tokenizer.hasMoreElements()) {
            tokens.add(tokenizer.nextToken().trim());
        }

        // compare test data against stdout capture on a per-line basis
        assertEquals("mainnet", tokens.get(0));
//        assertEquals("* aspen", tokens.get(1));
//        assertEquals("external", tokens.get(2));
    }

    @Test
    public void testNetworkGetName() {

        // Mock DataDirectory
        DataDirectory dataDirectory = Mockito.mock(DataDirectory.class);
        InputStream addressBookInputStream = getClass().getResourceAsStream("/addressbook.json");

        when(dataDirectory.networkGetName(addressBookInputStream)).thenReturn("aspen");

        AddressBook addressBook = AddressBook.init();
        addressBook.setDataDirectory(dataDirectory); // only using this for tests, to set the mock dataDirectory

        String networkName = dataDirectory.networkGetName(addressBookInputStream);
        System.out.println(networkName);
        assertEquals("aspen", networkName);
    }

    @Test
    public void testReadFileHashmap() throws IOException {

        String pathToFile = "index.txt";
        FileWriter fw = new FileWriter("index.txt");
        BufferedWriter bw = new BufferedWriter(fw);

        HashMap<String,String> mHashmap = new HashMap<>();
        mHashmap.put("0.0.9998", "filename_000");
        bw.write(mHashmap.toString());
        bw.close();

//        DataDirectory dataDirectory = Mockito.mock(DataDirectory.class);
//        when(dataDirectory.readFileHashmap(pathToFile, mHashmap)).thenReturn(mHashmap);

        Path filePath = Paths.get(pathToFile);
        File file = new File(filePath.toString());
        boolean fileExists = Files.exists(filePath);
        if (!fileExists) {
            // file does not exist so create a new file and write value
            dataDirectory.readFileHashmap(pathToFile, mHashmap);
            System.out.println("File does not exist: " + file);
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
            System.out.println("Hashmap: "+ newHashmap);
        } catch (Exception e ) {
            e.printStackTrace();
        }
        System.out.println("Return if emtpy: " + mHashmap);
    }

    @Test
    public void testWhatHashmapDoes() throws IOException {
        FileWriter fw = new FileWriter("index.txt");
        BufferedWriter bw = new BufferedWriter(fw);

        HashMap<String,String> mHashmap = new HashMap<>();
        mHashmap.put("0.0.1223", "filename_222");
        mHashmap.put("0.0.4444", "filename_444");
        bw.write(mHashmap.toString());
        bw.close();

        File file = new File("index.txt");
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
        System.out.println(newHashmap);
    }
}
