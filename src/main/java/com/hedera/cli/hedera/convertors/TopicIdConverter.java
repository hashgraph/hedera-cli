package com.hedera.cli.hedera.convertors;

import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TopicIdConverter implements Converter<String, ConsensusTopicId> {
    @Override
    public ConsensusTopicId convert(@NotNull String source) {
        return ConsensusTopicId.fromString(source);
    }
}
