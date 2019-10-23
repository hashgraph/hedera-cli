package com.hedera.cli.hedera.setup;

import com.hedera.cli.hedera.botany.AdjectivesWordListHelper;
import com.hedera.cli.hedera.botany.BotanyWordListHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class RandomNameGeneratorTest {

    @InjectMocks
    RandomNameGenerator randomNameGenerator;


    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetRandomName() {
        RandomNameGenerator spy = spy(randomNameGenerator);
        when(spy.getRandomName()).thenReturn("thorny_bluebell_8443");
        String helloSpy = spy.getRandomName();
        assertEquals("thorny_bluebell_8443", helloSpy);
        verify(spy).getRandomName();

        String randomNameActual = randomNameGenerator.getRandomName();
        List<String> botanyWordList = BotanyWordListHelper.words;
        List<String> adjectivesWordList = AdjectivesWordListHelper.words;
        int high = 10000;
        String adjectives = randomNameActual.split("_")[0];
        String botany = randomNameActual.split("_")[1];
        int number = Integer.valueOf(randomNameActual.split("_")[2]);
        assertTrue(botanyWordList.contains(botany));
        assertTrue(adjectivesWordList.contains(adjectives));
        assertTrue(high >= number);
    }
}
