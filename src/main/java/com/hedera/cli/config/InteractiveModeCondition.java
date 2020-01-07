package com.hedera.cli.config;

import com.hedera.cli.services.ExecutionService;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class InteractiveModeCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return ExecutionService.getInteractiveMode();
    }

}