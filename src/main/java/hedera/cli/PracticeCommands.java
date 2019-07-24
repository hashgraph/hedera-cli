package hedera.cli;

import hedera.cli.shell.ProgressBar;
import hedera.cli.shell.ProgressCounter;
import hedera.cli.shell.ShellHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class PracticeCommands {

    @Autowired
    ShellHelper shellHelper;

    @Autowired
    ProgressBar progressBar;

    @Autowired
    ProgressCounter progressCounter;

    @ShellMethod(value = "whatever")
    public void myCommand() {
        MyCommand myCommand = new MyCommand();
        myCommand.run();
    }


    @ShellMethod(value = "Do Something.", group = "Explicit Group Method Level 3")
    public void explicit3() {

    }

    @ShellMethod(value = "Do Something Else")
    public void implicit3() {

    }

    @ShellMethod("Add two integers together.")
    public String add(String a, String b) {
        return a + b;
    }

    @ShellMethod("Displays greeting message to the user whose name is supplied")
    public String echo(@ShellOption({"-N", "--name"}) String name) {
        String message = String.format("Hello %s!", name);
        shellHelper.print(message.concat(" (Default style message)"));
        shellHelper.printError(message.concat(" (Error style message)"));
        shellHelper.printWarning(message.concat(" (Warning style message)"));
        shellHelper.printInfo(message.concat(" (Info style message)"));
        shellHelper.printSuccess(message.concat(" (Success style message)"));

        String output = shellHelper.getSuccessMessage(message);
        return output.concat(" You are running spring shell hedera-cli.");
    }

    @ShellMethod("Displays progress spinner")
    public void progressSpinner() throws InterruptedException {
        for (int i = 1; i <=100; i++) {
            progressCounter.display();
            Thread.sleep(100);
        }
        progressCounter.reset();
    }

    @ShellMethod("Displays progress counter (with spinner)")
    public void progressCounter() throws InterruptedException {
        for (int i = 1; i <=100; i++) {
            progressCounter.display(i, "Processing");
            Thread.sleep(100);
        }
        progressCounter.reset();
    }

    @ShellMethod("Displays progress bar")
    public void progressBar() throws InterruptedException {
        for (int i = 1; i <=100; i++) {
            progressBar.display(i);
            Thread.sleep(100);
        }
        progressBar.reset();
    }
}