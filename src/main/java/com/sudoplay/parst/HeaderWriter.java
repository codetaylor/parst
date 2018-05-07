package com.sudoplay.parst;

import java.io.IOException;
import java.io.Writer;

public class HeaderWriter {

  public void write(String[] headers, Writer writer) throws IOException {

    if (headers != null && headers.length > 0) {

      for (String header : headers) {
        writer.write(header);
        writer.write(System.lineSeparator());
      }

      writer.write(System.lineSeparator());
    }

  }

}
