package com.hedera.cli.hedera.file;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import picocli.CommandLine;

@ExtendWith(MockitoExtension.class)
public class FileTest {

    private final PrintStream stdout = System.out;
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    @InjectMocks
    private File file;

    @Mock
    private FileCreate fileCreate;

    @Mock
    private FileDelete fileDelete;

    private List<String> expected = Arrays.asList("Usage: file [COMMAND]", "Create, update, delete file.",
            "file create <args> OR", "file update <args> OR", "file delete <args> OR", "Commands:",
            "create  Creates a new File and returns a FileID in the format of", "shardNum.realmNum.fileNum",
            "delete  Deletes specified file from the Hedera network");

    @BeforeEach
    public void setUp() throws UnsupportedEncodingException {
        System.setOut(new PrintStream(output, true, "UTF-8"));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(stdout);
    }

    @Test
    public void run() {
        assertNotNull(file);

        file.run();

        String outputResult = new String(output.toByteArray());
        List<String> outputResultArray = Arrays.asList(outputResult.split("\n"));
        outputResultArray.stream().map(s -> s.trim()).collect(Collectors.toList());
        outputResultArray.replaceAll(s -> s.trim());
        assertThat(outputResultArray, containsInAnyOrder(expected.toArray()));
    }

    @Test
    public void handleOthers() {
        CommandLine fileCreateCmd = new CommandLine(fileCreate);
        fileCreateCmd = spy(fileCreateCmd);
        file.setFileCreateCmd(fileCreateCmd);

        file.handle("create", "-d=22-11-2019,21:21:21");

        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        verify(fileCreateCmd).execute(valueCapture.capture());
        String actual = valueCapture.getValue();
        String expected = "-d=22-11-2019,21:21:21";
        assertEquals(expected, actual);
    }
}
