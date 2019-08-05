package com.hedera.cli.decorators;

public interface IDataSource {

  void writeData(String fileName, String value);

  String readData();

}