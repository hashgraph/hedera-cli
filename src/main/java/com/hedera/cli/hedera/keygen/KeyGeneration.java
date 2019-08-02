package com.hedera.cli.hedera.keygen;

import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;

import org.springframework.stereotype.Component;

import picocli.CommandLine.Command;

@Component
@Command(name = "generate",
         description = "@|fg(magenta) Transfer hbars to a single account|@%n",
         helpCommand = true)
public class KeyGeneration implements Runnable {

  @Override
  public void run() {
    System.out.println("KeyGeneration");
    var newKey = Ed25519PrivateKey.generate();
    var newPublicKey = newKey.getPublicKey();

    System.out.println("private key = " + newKey);
    System.out.println("public key = " + newPublicKey);
  }

}