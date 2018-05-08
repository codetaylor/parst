package com.sudoplay.parst;

public interface ILogger {

  void info(String message);

  void warn(String message);

  void error(String message);

  void error(String message, Throwable t);

}
