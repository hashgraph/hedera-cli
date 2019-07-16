package hedera.command;

import hedera.cli.shell.ShellHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class EchoCommand {

    @Autowired
    ShellHelper shellHelper;

    @ShellMethod("Displays greeting message")
    public String echo(@ShellOption({"-N", "--name"}) String name) {
        return String.format("Hello %s! Running spring shell", name);
    }
}
