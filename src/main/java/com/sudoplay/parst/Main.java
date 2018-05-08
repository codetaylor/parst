package com.sudoplay.parst;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sudoplay.parst.data.ConfigurationData;
import com.sudoplay.parst.data.FileData;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {

  public static void main(String[] args) {

    List<String> configFileLocations = new ArrayList<>();

    if (args.length == 0) {
      configFileLocations.add("config.json");

    } else {
      configFileLocations.addAll(Arrays.asList(args));
    }

    ILogger logger = new ConsoleLogger();

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    ConfigurationDataLoader configurationDataLoader = new ConfigurationDataLoader(gson);

    DataParser dataParser = new DataParser();
    HeaderWriter headerWriter = new HeaderWriter();
    DataWriter dataWriter = new DataWriter();

    for (String configFileLocation : configFileLocations) {

      logger.info("Parsing config: " + configFileLocation);

      try {
        ConfigurationData data = configurationDataLoader.load(configFileLocation);

        for (FileData fileData : data.fileDataList) {
          Path source = Paths.get(data.sourceFolder, fileData.source);
          Path target = Paths.get(data.targetFolder, fileData.target);

          logger.info("Parsing file: " + source + " >> " + target);

          if (!Files.exists(source)) {
            logger.warn("Missing file: " + source);
            continue;
          }

          Files.createDirectories(Paths.get(data.targetFolder));

          BufferedReader reader = Files.newBufferedReader(source);
          ParsedData parsedData = dataParser.parse(reader);
          reader.close();

          Map<String, LinkedHashMap<String, String>> processorMap = Util.copyProcessorMap(data.processorMap);

          if (fileData.processorMap != null) {
            Util.overrideProcessorMap(processorMap, fileData.processorMap);
          }

          ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

          BufferedWriter writer = Files.newBufferedWriter(target);
          headerWriter.write(fileData.header, writer);
          dataWriter.write(
              parsedData.getNameList(),
              parsedData.getMetaDataList(),
              parsedData.getRecordList(),
              data.processorFolder,
              processorMap,
              writer,
              engine,
              gson,
              logger
          );
          writer.close();
        }

      } catch (Exception e) {
        logger.error("Error processing config file: " + configFileLocation, e);
      }
    }

  }

}
