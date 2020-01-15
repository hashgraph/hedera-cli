package com.hedera.cli.config;

import com.hedera.cli.services.NonREPLHelper;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;


public class NonInteractiveModeCondition implements Condition {

    @Override
    public boolean matches(final ConditionContext context, final AnnotatedTypeMetadata metadata) {
        return !NonREPLHelper.getInteractiveMode();
    }

}