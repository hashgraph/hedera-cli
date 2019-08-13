package com.hedera.cli.hedera.setup;

import com.hedera.cli.hedera.utils.DataDirectory;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SetupTest {

    @Test
    public void testGetRandomName() {
        Setup setup = new Setup();
        String name = setup.getRandomName();
        System.out.println(name);
    }

    @Test
    public void testWriteBotanyFilename() {

        // TODO test fails if ~/.hedera does not exist
//        String userHome = System.getProperty("user.home");
//        String directoryName = ".hedera";
//
//        DataDirectory dataDirectory = new DataDirectory();
//        Setup setup = new Setup();
//
//        String fileName = setup.getRandomName();
//        String networkName = dataDirectory.readFile("network.txt");
//        System.out.println(networkName);
//
//        // write the data
//        Path filePath = Paths.get(userHome, directoryName, networkName, fileName);
//        File file = new File(filePath.toString());
//        System.out.println(file);
//
//        // compare with datadirectory writefile
//        dataDirectory.writeFile(fileName, "0.0.blah");
    }

    @Test
    public void testReadBotanyFilename() {
//        dataDirectory.readFile
    }
}
