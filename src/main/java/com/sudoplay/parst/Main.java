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

  public static final boolean USE_ASCII = false;

  public static void main(String[] args) {

    Main app = new Main();
    app.processConfigurationFiles(args);
  }

  private void processConfigurationFiles(String[] args) {

    List<String> configFileLocations = new ArrayList<>();

    if (args.length == 0) {
      configFileLocations.add("config.json");

    } else {
      configFileLocations.addAll(Arrays.asList(args));
    }

    ILogger logger = new ConsoleLogger();

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    ConfigurationDataLoader configurationDataLoader = new ConfigurationDataLoader(gson);
    CSVConverter converter = new CSVConverter(logger);
    DataParser dataParser = new DataParser();
    HeaderWriter headerWriter = new HeaderWriter();
    DataWriter dataWriter = new DataWriter(logger);

    for (String configFileLocation : configFileLocations) {

      logger.info(String.format("Reading config [%s]", configFileLocation));

      try {
        ConfigurationData data = configurationDataLoader.load(configFileLocation);

        if (data.extract != null) {
          String targetFolder = data.extract.targetFolder;

          if (targetFolder == null || targetFolder.isEmpty()) {
            targetFolder = data.transform.sourceFolder;
          }

          converter.extractCSV(data.extract.sourceFolder, targetFolder, data.extract.files);
        }

        for (FileData fileData : data.transform.fileDataList) {
          Path source = Paths.get(data.transform.sourceFolder, fileData.source);
          Path target = Paths.get(data.transform.targetFolder, fileData.target);

          logger.info("");

          if (Main.USE_ASCII) {
            logger.info(String.format("█ [%s] >> [%s]", source, target));

          } else {
            logger.info(String.format("F [%s] >> [%s]", source, target));
          }

          if (!Files.exists(source)) {
            logger.warn(String.format("Missing file: [%s]", source));
            continue;
          }

          Files.createDirectories(Paths.get(data.transform.targetFolder));

          BufferedReader reader = Files.newBufferedReader(source);
          ParsedData parsedData = dataParser.parse(reader);
          reader.close();

          Map<String, LinkedHashMap<String, String>> processorMap = Util.copyProcessorMap(data.transform.processorMap);

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
              data.transform.processorFolder,
              processorMap,
              writer,
              engine,
              gson
          );
          writer.close();
        }

      } catch (Exception e) {
        logger.error(String.format("Error processing config file: [%s]", configFileLocation), e);
      }
    }
  }

}
