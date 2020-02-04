package com.hedera.cli.hedera.converters;

import com.hedera.cli.hedera.convertors.TopicIdConverter;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestTopicIdConverter {
    TopicIdConverter converter = new TopicIdConverter();

    @Test
    public void validTopicIdConversion() {
        String topicIdString = "0.0.1001";
        ConsensusTopicId topicId = converter.convert(topicIdString);
        assertEquals(topicIdString, topicId.toString());
    }

    @Test
    public void invalidTopicThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            converter.convert("1001");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            converter.convert("0.0.");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            converter.convert("0.0.a");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            converter.convert(".0.1001");
        });
    }
}
