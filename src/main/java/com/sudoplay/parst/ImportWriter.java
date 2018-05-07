package com.sudoplay.parst;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

public class ImportWriter {

  public void write(Map<String, String> importTransformMap, Set<String> importSet, Writer writer) throws IOException {

    for (String line : importSet) {

      if (!importTransformMap.containsKey(line)) {
        System.out.println("[WARN]  Missing import transform entry for: " + line);
        continue;
      }

      String translatedLine = importTransformMap.get(line);

      if (translatedLine != null) {
        writer.write("import " + translatedLine + ";");
        writer.write(System.lineSeparator());
      }
    }
  }

}
