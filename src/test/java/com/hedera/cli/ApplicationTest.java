package com.hedera.cli;

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
    private Shell shell;

    @Autowired
    private ApplicationContext context;

    @Test
    public void contextLoads() {
        System.out.println("#############");
        System.out.println(context);
        System.out.println(shell);
        System.out.println("#############");
        // assertNotNull(context);
    }
}