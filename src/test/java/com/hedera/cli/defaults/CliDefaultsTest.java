package com.hedera.cli.defaults;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.shell.Availability;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class CliDefaultsTest {

    // captures our stdout
    @Rule
    public OutputCapture capture = new OutputCapture();

    static Logger logger = LogManager.getFormatterLogger();

    @Test
    public void noDefaultAccountSet() {
        CliDefaults defaults = Mockito.mock(CliDefaults.class, Mockito.CALLS_REAL_METHODS);
        Availability availability = defaults.isDefaultNetworkAndAccountSet(); 

        String expected = "you have not set your default account for the current network";
        assertEquals(expected, availability.getReason());
                
        // String actual = capture.toString().trim(); // trim, because a new line char is added in stdout
    }
}
