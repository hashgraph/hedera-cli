package com.hedera.cli.defaults;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.mockito.Mock;

import com.hedera.cli.hedera.utils.DataDirectory;

public class CliDefaultsTest {

    static Logger logger = LogManager.getFormatterLogger();

    @Mock
    DataDirectory dataDirectory;

    @Test
    public void testIsDefaultNetworkAndAccountSet() {

        System.out.println("hello");

        logger.debug("Debug Message Logged !!!");
        logger.info("Info Message Logged !!!");
        logger.error("Error Message Logged !!!");
        
        // mock a default account
        // DataDirectory dataDirectory = Mockito.mock(DataDirectory.class);
        // String currentNetwork = dataDirectory.readFile("network.txt", "aspen");
        // String pathToDefaultAccount = currentNetwork + File.separator + "accounts" + File.separator + "default.txt";
        // Mockito.doCallRealMethod()
        //     .when(dataDirectory)
        //     .writeFile(pathToDefaultAccount, "0.0.1001");

        // CliDefaults defaults = Mockito.mock(CliDefaults.class);
        // Mockito.doCallRealMethod()
        //         .when(defaults)
        //         .isDefaultNetworkAndAccountSet();

        
        // System.out.println("11111111111");
        // System.out.println(defaults.isDefaultNetworkAndAccountSet().getReason());
        // var actual = Availability.available().getReason();
        // assertEquals(actual, defaults.isDefaultNetworkAndAccountSet().getReason());
    }
}
