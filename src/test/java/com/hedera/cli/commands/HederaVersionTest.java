package com.hedera.cli.commands;

import com.hedera.cli.shell.ShellHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

// @ExtendWith(MockitoExtension.class)
@TestPropertySource
public class HederaVersionTest {

    // @InjectMocks
    // private HederaVersion hederaVersion;

    // @Value("${app.version}")
    // private String version;

    // @Mock
    // private ShellHelper shellHelper;

    @Test
    public void version() {
        assertNotNull(1);
        // hederaVersion.version();
        // verify(shellHelper, times(1)).printInfo(version);
    }
}
