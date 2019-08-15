package com.hedera.cli.hedera.setup;

import org.junit.Test;

public class SetupTest {

    @Test
    public void testGetRandomName() {
        Setup setup = new Setup();
        String name = setup.getRandomName();
        System.out.println(name);
    }

    @Test
    public void testWriteBotanyFilename() {
//        String userHome = System.getProperty("user.home");
//        String directoryName = ".hedera";
//
//        DataDirectory dataDirectory = new DataDirectory();
//        Setup setup = new Setup();
//
//        String fileName = setup.getRandomName();
//        String networkName = dataDirectory.readFile("network.txt");
//        System.out.println("network here is " + networkName);
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
    public void testSaveToJson() {
        Setup setup = new Setup();
        String accountId = "";
        String phrase = "";
        setup.saveToJson(accountId, phrase);

    }

}
