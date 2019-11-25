package com.hedera.cli.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import java.util.List;

import com.hedera.cli.hedera.file.File;

import com.hedera.cli.shell.ShellHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class HederaFileTest {

    @InjectMocks
    private HederaFile hederaFile;

    @Mock
    private ShellHelper shellHelper;

    @Mock
    private File file;

    private String c = "";
    private String d = "";
    private String s = "";
    private String t = "";
    private String fileId = "";

    @Test
    public void fileCreate() {
        hederaFile.file("create", fileId, c, d, s, t);

        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCapture2 = ArgumentCaptor.forClass(String.class);
        verify(file).handle(valueCapture.capture(), valueCapture2.capture());

        String actual = valueCapture.getValue();
        String expected = "create";
        assertEquals(expected, actual);

        List<String> varArgs = valueCapture2.getAllValues();
        int actual2 = varArgs.size();
        int expected2 = 0;
        assertEquals(expected2, actual2);
    }

    @Test
    public void fileDelete() {
        hederaFile.file("delete", fileId, c, d, s, t);

        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        verify(shellHelper).printError(valueCapture.capture());
        String actual = valueCapture.getValue();
        String expected = "Please provide a file id";
        assertEquals(expected, actual);
    }

    @Test
    public void fileInfo() {
        hederaFile.file("info", fileId, c, d, s, t);
        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        verify(shellHelper).printError(valueCapture.capture());
        String actual = valueCapture.getValue();
        String expected = "Please provide a file id";
        assertEquals(expected, actual);
    }
}