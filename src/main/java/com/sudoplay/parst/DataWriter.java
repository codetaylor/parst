package com.sudoplay.parst;

import org.apache.commons.csv.CSVRecord;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public class DataWriter {

  public void write(
      List<String> nameList,
      List<String> importList,
      List<CSVRecord> recordList,
      BufferedWriter writer
  ) throws IOException {

    for (int i = 1; i < nameList.size(); i++) {
      writer.write(System.lineSeparator());

      String prefixName = nameList.get(0);
      String prefix = "";

      if (prefixName != null && prefixName.length() > 0) {
        prefix = prefixName.toUpperCase() + "_";
      }

      writer.write("global " + prefix + nameList.get(i)
          .toUpperCase() + " as " + importList.get(i) + "[" + importList.get(0) + "] {");
      writer.write(System.lineSeparator());

      for (int j = 0; j < recordList.size(); j++) {

        CSVRecord record = recordList.get(j);
        writer.write("  " + record.get(0) + ": " + record.get(i));

        if (j < recordList.size() - 1) {
          writer.write(",");
        }
        writer.write(System.lineSeparator());
      }

      writer.write("};");
      writer.write(System.lineSeparator());
    }

  }

}
