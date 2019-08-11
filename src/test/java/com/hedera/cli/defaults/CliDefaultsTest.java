package com.hedera.cli.defaults;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.shell.Availability;

import static org.junit.Assert.assertEquals;

import com.hedera.cli.hedera.utils.DataDirectory;

@RunWith(MockitoJUnitRunner.class)
public class CliDefaultsTest {

    // captures our stdout
    @Rule
    public OutputCapture capture = new OutputCapture();

    static Logger logger = LogManager.getFormatterLogger();

    @Mock
    DataDirectory dataDirectory;

    @Test
    public void noDefaultAccountSet() {
        CliDefaults defaults = Mockito.mock(CliDefaults.class, Mockito.CALLS_REAL_METHODS);
        Availability availability = defaults.isDefaultNetworkAndAccountSet(); 

        String expected = "Please set your default account in current network";
        String actual = capture.toString().trim(); // trim, because a new line char is added in stdout
        assertEquals(expected, actual);
        assertEquals(null, availability.getReason());
    }
}
