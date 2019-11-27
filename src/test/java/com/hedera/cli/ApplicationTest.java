package com.hedera.cli;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.shell.Shell;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@Import(ApplicationTestRunner.class)
@ComponentScan("com.hedera.cli")
public class ApplicationTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private Shell shell;

    @Test
    public void contextLoads() {
        Application.main(new String[] {});
        assertNotNull(context);
    }

    @Test
    public void shellLoads() {
        assertNotNull(shell);
    }
}