package com.hedera.cli.models;

import com.hedera.cli.hedera.botany.AdjectivesWordListHelper;
import com.hedera.cli.hedera.botany.BotanyWordListHelper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class RandomNameGenerator {

    public String getRandomName() {
        Random rand = new Random();
        List<String> botanyNames = BotanyWordListHelper.words;
        List<String> adjectives = AdjectivesWordListHelper.words;
        String randomBotanyName = botanyNames.get(rand.nextInt(botanyNames.size()));
        String randomAdjectives = adjectives.get(rand.nextInt(adjectives.size()));
        int randomNumber = rand.nextInt(10000);
        return randomAdjectives + "_" + randomBotanyName + "_" + randomNumber;
    }
}
