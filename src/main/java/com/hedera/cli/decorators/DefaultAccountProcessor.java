package com.hedera.cli.decorators;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;

@SupportedAnnotationTypes("com.hedera.cli.decorators.DefaultAccount")
public class DefaultAccountProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    System.out.println("DefaultAccountProcessor invoked");
		return false;
	}

}