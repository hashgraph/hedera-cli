package com.hedera.cli.defaults;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hedera.cli.hedera.utils.DataDirectory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.shell.Availability;

@RunWith(MockitoJUnitRunner.class)
public class CliDefaultsTest {

    // captures our stdout
    @Rule
    public OutputCapture capture = new OutputCapture();

    static Logger logger = LogManager.getFormatterLogger();

    @Test
    public void testIsDefaultNetworkAndAccountSet() {

        DataDirectory dataDirectory = mock(DataDirectory.class);
        when(dataDirectory.readFile("network.txt", "testnet")).thenReturn("testnet");

        CliDefaults defaults = new CliDefaults() {
            @Override
            public Availability isDefaultNetworkAndAccountSet() {
                return super.isDefaultNetworkAndAccountSet();
            }
        };
        defaults.setDataDirectory(dataDirectory);
        Availability availability = defaults.isDefaultNetworkAndAccountSet();
        assertEquals(Availability.available().getReason(), availability.getReason());
    }
}

