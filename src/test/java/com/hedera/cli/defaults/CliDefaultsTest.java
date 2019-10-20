package com.hedera.cli.defaults;

import com.hedera.cli.hedera.utils.DataDirectory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.shell.Availability;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CliDefaultsTest {

    // captures our stdout
    @Rule
    public OutputCapture capture = new OutputCapture();

    @Test
    public void testIsDefaultNetworkAndAccountSet() {

        DataDirectory dataDirectory = Mockito.mock(DataDirectory.class);
        when(dataDirectory.readFile("network.txt", "aspen")).thenReturn("aspen");

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

