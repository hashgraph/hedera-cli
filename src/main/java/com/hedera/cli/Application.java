/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.hedera.cli;

import java.io.File;
import java.util.List;

import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.utils.DataDirectory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        // set defaults
        DataDirectory dataDirectory = new DataDirectory();
        dataDirectory.readFile("network.txt", "aspen");
        Hedera hedera = new Hedera();
        List<String> networkList = hedera.getNetworksStrings();
        for (String network: networkList) {
            String accountsDirForNetwork = network + File.separator + "accounts";
            dataDirectory.mkHederaSubDir(accountsDirForNetwork);
        }

        // let Spring instantiate and inject dependencies
        System.exit(SpringApplication.exit(SpringApplication.run(Application.class, args)));
    }
}