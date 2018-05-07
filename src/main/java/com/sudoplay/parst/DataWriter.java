package com.sudoplay.parst;

import org.apache.commons.csv.CSVRecord;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DataWriter {

  public void write(
      List<String> nameList,
      List<Map<String, String>> parsedMetaDataList,
      List<CSVRecord> recordList,
      Map<String, Map<String, String>> processorMap,
      BufferedWriter writer,
      ScriptEngine engine
  ) throws IOException {

    Bindings bindings = engine.createBindings();
    bindings.put("nameList", nameList);
    bindings.put("metaList", parsedMetaDataList);
    bindings.put("recordList", recordList);
    bindings.put("writer", writer);

    this.fireProcessors(Collections.emptyMap(), processorMap.get("preFile"), engine, bindings);

    for (int i = 1; i < nameList.size(); i++) {

      String prefixName = nameList.get(0);
      String prefix = "";

      if (prefixName != null && prefixName.length() > 0) {
        prefix = prefixName.toUpperCase() + "_";
      }

      String collectionName = prefix + nameList.get(i).toUpperCase();

      bindings.put("columnIndex", i);
      bindings.put("name", nameList.get(i));
      bindings.put("collectionName", collectionName);
      bindings.put("meta", parsedMetaDataList.get(i));

      this.fireProcessors(parsedMetaDataList.get(i), processorMap.get("preCollection"), engine, bindings);
      this.fireProcessors(parsedMetaDataList.get(i), processorMap.get("collection"), engine, bindings);
      this.fireProcessors(parsedMetaDataList.get(i), processorMap.get("postCollection"), engine, bindings);
    }

    bindings.remove("columnIndex");
    bindings.remove("name");
    bindings.remove("collectionName");
    bindings.remove("meta");

    this.fireProcessors(Collections.emptyMap(), processorMap.get("postFile"), engine, bindings);
  }

  private void fireProcessors(
      Map<String, String> metaMap,
      Map<String, String> processorMap,
      ScriptEngine engine,
      Bindings bindings
  ) throws FileNotFoundException {

    if (processorMap == null) {
      return;
    }

    for (Map.Entry<String, String> entry : processorMap.entrySet()) {

      String key = entry.getKey();

      if (metaMap.containsKey(key) || "all".equals(key)) {
        String processor = entry.getValue();

        try {
          engine.eval(new FileReader(processor), bindings);

        } catch (ScriptException e) {
          e.printStackTrace();
        }
      }
    }
  }

}
