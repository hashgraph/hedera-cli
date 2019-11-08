package com.hedera.cli.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@TestPropertySource
public class HederaVersionTest {

    @InjectMocks
    private HederaVersion hederaVersion;

    @Mock
    private Environment env;

    @Test
    public void version() {
        hederaVersion.version();
        verify(env, times(1)).getProperty("info.app.version");
    }
}
