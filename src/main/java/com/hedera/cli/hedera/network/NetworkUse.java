package com.hedera.cli.hedera.network;

import com.hedera.cli.hedera.utils.DataDirectory;

import com.hedera.cli.shell.ShellHelper;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@NoArgsConstructor
@Component
@Command(name = "use",
         description = "@|fg(225) Set specific Hedera network we will use.|@",
         helpCommand = true)
public class NetworkUse implements Runnable {

  @Autowired
  private DataDirectory dataDirectory;

  @Autowired
  private ShellHelper shellHelper;

  @Parameters(index = "0", description = "Name of the network"
          + "%n@|bold,underline Usage:|@%n"
          + "@|fg(yellow) network use mainnet|@")
  private String name;
  
  @Override
  public void run() {
    shellHelper.printSuccess("Setting network to " + name);
    dataDirectory.writeFile("network.txt", name);
  }

}