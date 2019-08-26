package com.hedera.cli.hedera.setup;

import org.junit.Test;

public class SetupTest {

    @Test
    public void testGetRandomName() {
        Setup setup = new Setup();
        String name = setup.getRandomName();
        System.out.println(name);
    }

    @Test
    public void testSaveToJson() {
        Setup setup = new Setup();
        String accountId = "";
        String phrase = "";
//        setup.saveToJson(accountId, phrase);
    }

}
