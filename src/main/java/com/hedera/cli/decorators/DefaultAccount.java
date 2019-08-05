package com.hedera.cli.decorators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE) // class annotation
public @interface DefaultAccount {

  // boolean writeData() default true;

  // String filename();
  // String value();
  // String readData();

  // public DefaultAccount(Runnable runnable) {
  //   System.out.println("DefaultAccount decorator is invoked");
  // }

  // @Override
  // public void writeData(String fileName, String value) {

  // }

  // @Override
  // public String readData() {
  //   return null;
  // }

}