package com.hedera.cli.config;

import org.jline.reader.LineReader;
import org.springframework.util.StringUtils;

public class InputReader {
  public static final Character DEFAULT_MASK = '*';

  private Character mask;
  private LineReader lineReader;

  public InputReader(LineReader lineReader) {
      this(lineReader, null);
  }

  public InputReader(LineReader lineReader, Character mask) {
      this.lineReader = lineReader;
      this.mask = mask != null ? mask : DEFAULT_MASK;
  }

  public String prompt(String  prompt) {
      return prompt(prompt, null, true);
  }

  public String prompt(String  prompt, String defaultValue) {
      return prompt(prompt, defaultValue, true);
  }

  public String prompt(String  prompt, String defaultValue, boolean echo) {
      String answer = "";
      if (echo) {
          answer = lineReader.readLine(prompt + ": ");
      } else {
          answer = lineReader.readLine(prompt + ": ", mask);
      }
      if (StringUtils.isEmpty(answer)) {
          return defaultValue;
      }
      return answer;
  }
}