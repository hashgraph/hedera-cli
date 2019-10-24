package com.hedera.cli.hedera;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.hedera.cli.services.CurrentAccountService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = { CurrentAccountService.class })
public class HederaTest {


    @InjectMocks
    private Hedera hedera;

    @Test
    public void hedera() {
        assertNotNull(hedera);
    }
}
