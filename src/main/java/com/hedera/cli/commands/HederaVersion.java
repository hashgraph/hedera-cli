package com.hedera.cli.commands;

import com.hedera.cli.defaults.CliDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@ShellComponent
@PropertySource("classpath:application.properties")
public class HederaVersion extends CliDefaults {

    @Autowired
    private Environment env;

    @ShellMethodAvailability("isDefaultNetworkAndAccountSet")
    @ShellMethod(value = "hedera-cli version")
    public void version() {
        System.out.println(readProperty());
    }

    public String readProperty() {
        return env.getProperty("info.app.version");
    }
}
