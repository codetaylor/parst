package com.sudoplay.parst;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ConsoleLogger implements ILogger {

  @Override
  public void info(String message) {

    System.out.println("[INFO]  " + message);
  }

  @Override
  public void warn(String message) {

    System.out.println("[WARN]  " + message);
  }

  @Override
  public void error(String message) {

    System.out.println("[ERROR] " + message);
  }

  @Override
  public void error(String message, Throwable t) {

    System.out.println("[ERROR] " + message + ": " + t.getMessage());

    StringWriter errors = new StringWriter();
    t.printStackTrace(new PrintWriter(errors));
    System.out.println(errors.toString());
  }
}
