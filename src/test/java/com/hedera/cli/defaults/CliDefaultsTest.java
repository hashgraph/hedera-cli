package com.hedera.cli.defaults;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.shell.Availability;

import static org.junit.Assert.assertEquals;


public class CliDefaultsTest {

    @Test
    public void testIsDefaultNetworkAndAccountSet() {
        CliDefaults defaults = Mockito.mock(CliDefaults.class);
        Mockito.doCallRealMethod()
                .when(defaults)
                .isDefaultNetworkAndAccountSet();

        var actual = Availability.available().getReason();
        assertEquals(actual, defaults.isDefaultNetworkAndAccountSet().getReason());
    }
}
