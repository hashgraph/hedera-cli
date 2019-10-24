package com.hedera.cli.hedera.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Calendar;
import java.util.TimeZone;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.util.FileSystemUtils;

public class UtilsTest {

    @TempDir
    public Path tempDir;

    private DataDirectory dataDirectory;

    // class under test
    private Utils utils;

    @BeforeEach
    public void setUp() {
        // manual invocation of DataDirectory
        dataDirectory = new DataDirectory();
        dataDirectory.setDataDir(tempDir);
        utils = new Utils();
        utils.setDataDirectory(dataDirectory);
    }

    @AfterEach
    public void tearDown() throws IOException {
        FileSystemUtils.deleteRecursively(tempDir);
    }

    @Test
    public void dateToMilliseconds() throws ParseException {
        // raw test data
        String[] dateInString = { "22-02-2019", "21:30:58" };

        // prepare test data
        TimeZone tz = Calendar.getInstance().getTimeZone();
        StringBuilder sb = new StringBuilder();
        sb.append("2019-02-22T21:30:58+08:00[");
        sb.append(tz.getID());
        sb.append("]");
        String expected = sb.toString();

        // execute the function that we want to test
        ZonedDateTime zonedDateTime = utils.dateToMilliseconds(dateInString);
        String actual = zonedDateTime.toString();

        // assert
        assertEquals(expected, actual);
    }
}
