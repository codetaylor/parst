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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataWriter {

  private final ILogger logger;

  public DataWriter(ILogger logger) {

    this.logger = logger;
  }

  public void write(
      List<String> nameList,
      List<String> metaDataList,
      List<CSVRecord> recordList,
      String processorFolder,
      Map<String, LinkedHashMap<String, String>> processorMap,
      BufferedWriter writer,
      ScriptEngine engine,
      Gson gson
  ) throws Exception {

    Bindings bindings = engine.createBindings();
    bindings.put("logger", this.logger);
    bindings.put("nameList", nameList);
    bindings.put("metaList", metaDataList);
    bindings.put("recordList", recordList);
    bindings.put("writer", writer);

    this.fireProcessors("preFile", processorFolder, processorMap, engine, bindings);

    for (int i = 1; i < nameList.size(); i++) {

      if (Main.USE_ASCII) {
        this.logger.info(String.format("├─▓ [%d : %s]", i, nameList.get(i)));

      } else {
        this.logger.info(String.format("+-C [%d : %s]", i, nameList.get(i)));
      }

      String meta = metaDataList.get(i);
      ColumnProcessorData columnProcessorData = gson.fromJson(meta, ColumnProcessorData.class);

      Map<String, LinkedHashMap<String, String>> columnProcessorMap = Util.copyProcessorMap(processorMap);
      Util.overrideProcessorMap(columnProcessorMap, columnProcessorData.processorMap);

      bindings.put("columnIndex", i);
      bindings.put("name", nameList.get(i));
      bindings.put("meta", meta);

      this.fireProcessors("preCollection", processorFolder, processorMap, engine, bindings);
      this.fireProcessors("collection", processorFolder, processorMap, engine, bindings);
      this.fireProcessors("postCollection", processorFolder, processorMap, engine, bindings);
    }

    bindings.remove("columnIndex");
    bindings.remove("name");
    bindings.remove("meta");

    this.fireProcessors("postFile", processorFolder, processorMap, engine, bindings);
  }

  private void fireProcessors(
      String phase,
      String processorFolder,
      Map<String, LinkedHashMap<String, String>> processorMap,
      ScriptEngine engine,
      Bindings bindings
  ) throws IOException {

    boolean isFinal = "postFile".equals(phase) || "postCollection".equals(phase);
    boolean indent = !"preFile".equals(phase) && !"postFile".equals(phase);

    String indentString = "";

    if (indent) {

      if (Main.USE_ASCII) {
        indentString = "│ ";

      } else {
        indentString = "| ";
      }
    }

    if (Main.USE_ASCII) {

      if (isFinal) {
        this.logger.info(String.format("%s└─▒ [%s]", indentString, phase));

      } else {
        this.logger.info(String.format("%s├─▒ [%s]", indentString, phase));
      }

    } else {
      this.logger.info(String.format("%s+-P [%s]", indentString, phase));
    }

    this.fireProcessors(processorFolder, processorMap.get(phase), engine, bindings, isFinal, indent);
  }

  private void fireProcessors(
      String processorFolder,
      Map<String, String> processorMap,
      ScriptEngine engine,
      Bindings bindings,
      boolean isFinal,
      boolean indent
  ) throws IOException {

    if (processorMap == null) {
      return;
    }

    int index = 0;
    Collection<String> processors = processorMap.values();

    for (String processor : processors) {

      boolean isLast = (index == processors.size() - 1);
      index += 1;

      if (processor == null) {
        continue;
      }

      Path path = this.getProcessorPath(processorFolder, processor);

      BufferedReader reader = null;

      try {
        long start = System.currentTimeMillis();
        reader = Files.newBufferedReader(path);
        engine.eval(reader, bindings);
        long duration = System.currentTimeMillis() - start;

        String indentString = "";

        if (indent) {

          if (Main.USE_ASCII) {
            indentString = "│ ";

          } else {
            indentString = "| ";
          }
        }

        if (Main.USE_ASCII) {

          if (isFinal) {

            if (isLast) {
              this.logger.info(String.format("%s  └─> [%s] [%dms]", indentString, processor, duration));

            } else {
              this.logger.info(String.format("%s  ├─> [%s] [%dms]", indentString, processor, duration));
            }

          } else {

            if (isLast) {
              this.logger.info(String.format("%s│ └─> [%s] [%dms]", indentString, processor, duration));

            } else {
              this.logger.info(String.format("%s│ ├─> [%s] [%dms]", indentString, processor, duration));
            }
          }

        } else {

          if (isFinal) {
            this.logger.info(String.format("%s  +-> [%s] [%dms]", indentString, processor, duration));

          } else {
            this.logger.info(String.format("%s| +-> [%s] [%dms]", indentString, processor, duration));
          }
        }

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
