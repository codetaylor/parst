package com.sudoplay.parst;

import com.google.gson.Gson;
import com.sudoplay.parst.data.ColumnProcessorData;
import org.apache.commons.csv.CSVRecord;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataWriter {

  public void write(
      List<String> nameList,
      List<String> metaDataList,
      List<CSVRecord> recordList,
      String processorFolder,
      Map<String, LinkedHashMap<String, String>> processorMap,
      BufferedWriter writer,
      ScriptEngine engine,
      Gson gson,
      ILogger logger
  ) throws Exception {

    Bindings bindings = engine.createBindings();
    bindings.put("logger", logger);
    bindings.put("nameList", nameList);
    bindings.put("metaList", metaDataList);
    bindings.put("recordList", recordList);
    bindings.put("writer", writer);

    this.fireProcessors(processorFolder, processorMap.get("preFile"), engine, bindings);

    for (int i = 1; i < nameList.size(); i++) {

      String meta = metaDataList.get(i);
      ColumnProcessorData columnProcessorData = gson.fromJson(meta, ColumnProcessorData.class);

      Map<String, LinkedHashMap<String, String>> columnProcessorMap = Util.copyProcessorMap(processorMap);
      Util.overrideProcessorMap(columnProcessorMap, columnProcessorData.processorMap);

      bindings.put("columnIndex", i);
      bindings.put("name", nameList.get(i));
      bindings.put("meta", meta);

      this.fireProcessors(processorFolder, columnProcessorMap.get("preCollection"), engine, bindings);
      this.fireProcessors(processorFolder, columnProcessorMap.get("collection"), engine, bindings);
      this.fireProcessors(processorFolder, columnProcessorMap.get("postCollection"), engine, bindings);
    }

    bindings.remove("columnIndex");
    bindings.remove("name");
    bindings.remove("meta");

    this.fireProcessors(processorFolder, processorMap.get("postFile"), engine, bindings);
  }

  private void fireProcessors(
      String processorFolder,
      Map<String, String> processorMap,
      ScriptEngine engine,
      Bindings bindings
  ) throws IOException {

    if (processorMap == null) {
      return;
    }

    for (String processor : processorMap.values()) {

      if (processor == null) {
        continue;
      }

      Path path = this.getProcessorPath(processorFolder, processor);

      BufferedReader reader = null;

      try {
        reader = Files.newBufferedReader(path);
        engine.eval(reader, bindings);

      } catch (Throwable t) {
        t.printStackTrace();

      } finally {

        if (reader != null) {
          reader.close();
        }
      }
    }
  }

  private Path getProcessorPath(String processorFolder, String processor) {

    Path path = null;

    try {
      String name = Paths.get(processor).toString();

      name = name.replaceAll("\\\\", "/");

      if (!name.startsWith("/")) {
        name = "/" + name;
      }

      URL resource = this.getClass().getResource(name);

      if (resource != null) {
        path = Paths.get(resource.toURI());

        if (!Files.exists(path)) {
          path = null;
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    if (path == null) {
      path = Paths.get(processorFolder, processor);
    }

    return path;
  }

}
