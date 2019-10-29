package com.hedera.cli.hedera.crypto;

import com.hedera.cli.config.InputReader;

public interface Operation {
  public void executeSubCommand(InputReader inputReader, String... args);
}