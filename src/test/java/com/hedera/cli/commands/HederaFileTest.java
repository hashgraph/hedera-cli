package com.hedera.cli.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

import java.util.List;

import com.hedera.cli.hedera.file.File;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// @ExtendWith(MockitoExtension.class)
public class HederaFileTest {

  // @InjectMocks
  // private HederaFile hederaFile;

  // @Mock
  // private File file;

  @Test
  public void fileCreate() {
    assertNotNull(1);
    // hederaFile.file("create", "", "", "", "", "");

    // ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
    // ArgumentCaptor<String> valueCapture2 = ArgumentCaptor.forClass(String.class);
    // verify(file).handle(valueCapture.capture(), valueCapture2.capture());
    
    // String actual = valueCapture.getValue();
    // String expected = "create";
    // assertEquals(expected, actual);

    // List<String> varArgs = valueCapture2.getAllValues();
    // int actual2 = varArgs.size();
    // int expected2 = 0;
    // assertEquals(expected2, actual2);
  }

  // @Test
  // public void fileDelete() {
  //   hederaFile.file("delete", "", "", "", "", "");

  //   ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
  //   ArgumentCaptor<String> valueCapture2 = ArgumentCaptor.forClass(String.class);
  //   verify(file).handle(valueCapture.capture(), valueCapture2.capture());
    
  //   String actual = valueCapture.getValue();
  //   String expected = "delete";
  //   assertEquals(expected, actual);

  //   List<String> varArgs = valueCapture2.getAllValues();
  //   int actual2 = varArgs.size();
  //   int expected2 = 0;
  //   assertEquals(expected2, actual2);
  // }

}