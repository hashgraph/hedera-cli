package com.hedera.cli.shell;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PromptColorTest {

  @Test
  public void promptColor() {
    assertEquals(0, PromptColor.BLACK.ordinal());
    assertEquals(1, PromptColor.RED.ordinal());
    assertEquals(2, PromptColor.GREEN.ordinal());
    assertEquals(3, PromptColor.YELLOW.ordinal());
    assertEquals(4, PromptColor.BLUE.ordinal());
    assertEquals(5, PromptColor.MAGENTA.ordinal());
    assertEquals(6, PromptColor.CYAN.ordinal());
    assertEquals(7, PromptColor.WHITE.ordinal());
    assertEquals(8, PromptColor.BRIGHT.ordinal());

    assertEquals(0, PromptColor.valueOf("BLACK").toJlineAttributedStyle());
    assertEquals(1, PromptColor.valueOf("RED").toJlineAttributedStyle());
    assertEquals(2, PromptColor.valueOf("GREEN").toJlineAttributedStyle());
    assertEquals(3, PromptColor.valueOf("YELLOW").toJlineAttributedStyle());
    assertEquals(4, PromptColor.valueOf("BLUE").toJlineAttributedStyle());
    assertEquals(5, PromptColor.valueOf("MAGENTA").toJlineAttributedStyle());
    assertEquals(6, PromptColor.valueOf("CYAN").toJlineAttributedStyle());
    assertEquals(7, PromptColor.valueOf("WHITE").toJlineAttributedStyle());
    assertEquals(8, PromptColor.valueOf("BRIGHT").toJlineAttributedStyle());
  }

}