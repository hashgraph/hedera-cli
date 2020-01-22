package com.hedera.cli.hedera.hcs;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Getter
@Setter
@Component
@Command(name = "create")
public class CreateTopic implements Runnable {
    @Parameters(index = "0", description = "topic name"
            + "%n@|bold,underline Usage:|@%n" + "@|fg(yellow) hcs create helloworld|@")
    private String topic;

    @Override
    public void run() {
        System.out.println("The topic is " + topic);
        System.out.println("running hcs create");
        // write our HCS gRPC call here, which can be abstracted into a different class/function
    }

    public void handle(String subCommand, String... args) {
        new CommandLine(this).execute(args);
    }

}