package com.hedera.cli.defaults;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hedera.cli.commands.CommandBase;
import com.hedera.cli.models.DataDirectory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@ExtendWith(MockitoExtension.class)
public class CommandBaseTest {

    // Example Cli Test Class that extends CliDefaults abstract class
    private static class ExampleCli extends CommandBase {
        @ShellMethodAvailability("isNotCompleted")
        @ShellMethod(value = "example command")
        public void exampleCommmand() {
            System.out.println("Example stub function");
        }
    }

    @Test
    public void bothDefaultNetworkAndDefaultAccountAreSet() {
        DataDirectory dataDirectory = mock(DataDirectory.class);
        // default network is set as "testnet"
        when(dataDirectory.readFile("network.txt", "testnet")).thenReturn("testnet");
        // default account is set as "0.0.1234"
        when(dataDirectory.readFile("testnet/accounts/default.txt")).thenReturn("0.0.1234");

        ExampleCli exampleCli = new ExampleCli();
        exampleCli.setDataDirectory(dataDirectory);

        Availability availability = exampleCli.isDefaultNetworkAndAccountSet();
        assertEquals(Availability.available().getReason(), availability.getReason());
    }

    @Test
    public void defaultNetworkNotSet() {
        DataDirectory dataDirectory = mock(DataDirectory.class);
        // this should never happen, since network.txt will return "testnet" if "network.txt" is emptys
        // but we emulate the scenario when it is indeed empty
        // default network is not set for some reason
        when(dataDirectory.readFile("network.txt", "testnet")).thenReturn("");

        ExampleCli exampleCli = new ExampleCli();
        exampleCli.setDataDirectory(dataDirectory);

        Availability availability = exampleCli.isDefaultNetworkAndAccountSet();
        assertEquals("you have not set your default network", availability.getReason());
    }

    @Test
    public void defaultAccountNotSet() {
        DataDirectory dataDirectory = mock(DataDirectory.class);
        // default network is set
        when(dataDirectory.readFile("network.txt", "testnet")).thenReturn("testnet");
        // default account is NOT set
        when(dataDirectory.readFile("testnet/accounts/default.txt")).thenReturn("");
        
        ExampleCli exampleCli = new ExampleCli();
        exampleCli.setDataDirectory(dataDirectory);

        Availability availability = exampleCli.isDefaultNetworkAndAccountSet();
        assertEquals("you have not set your default account for the current network", availability.getReason());
    }

    @Test
    public void isNotCompleted() {
        ExampleCli exampleCli = new ExampleCli();
        assertEquals("it is not completed", exampleCli.isNotCompleted().getReason());
    }

}
