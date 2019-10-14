package com.hedera.cli.hedera.crypto;

import java.lang.reflect.Constructor;
import org.springframework.context.ApplicationContext;

import picocli.CommandLine;

public class PicocliSpringFactory implements CommandLine.IFactory {
  private final ApplicationContext applicationContext;

  public PicocliSpringFactory(ApplicationContext applicationContext) {
      this.applicationContext = applicationContext;
  }

  @Override
  public <K> K create(Class<K> aClass) throws Exception {
      try {
          return applicationContext.getBean(aClass);
      } catch (Exception e) {
          try {
              return aClass.getConstructor().newInstance();
          } catch (Exception ex) {
              Constructor<K> constructor = aClass.getDeclaredConstructor();
              constructor.setAccessible(true);
              return constructor.newInstance();
          }
      }
  }
}