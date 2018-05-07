package com.sudoplay.parst;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

  public static void main(String[] args) {

    List<String> configFileLocations = new ArrayList<>();

    if (args.length == 0) {
      configFileLocations.add("config.json");

    } else {
      configFileLocations.addAll(Arrays.asList(args));
    }

    ConfigurationDataLoader configurationDataLoader = new ConfigurationDataLoader(
        new GsonBuilder().setPrettyPrinting().create()
    );

    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

    DataParser dataParser = new DataParser();
    HeaderWriter headerWriter = new HeaderWriter();
    ImportWriter importWriter = new ImportWriter();
    DataWriter dataWriter = new DataWriter();

    for (String configFileLocation : configFileLocations) {

      try {
        ConfigurationData data = configurationDataLoader.load(configFileLocation);

        for (FileData fileData : data.fileDataList) {
          Path source = Paths.get(data.sourceFolder, fileData.source);
          Path target = Paths.get(data.targetFolder, fileData.target);

          if (!Files.exists(source)) {
            System.out.println("Missing file: " + source);
            continue;
          }

          Files.createDirectories(Paths.get(data.targetFolder));

          BufferedReader reader = Files.newBufferedReader(source);
          ParsedData parsedData = dataParser.parse(reader);
          reader.close();

          BufferedWriter writer = Files.newBufferedWriter(target);
          headerWriter.write(fileData.header, writer);
          importWriter.write(data.importMap, parsedData.getImportSet(), writer);
          dataWriter.write(
              parsedData.getNameList(),
              parsedData.getMetaDataList(),
              parsedData.getRecordList(),
              data.processorMap,
              writer,
              engine
          );
          writer.close();
        }

      } catch (Exception e) {
        e.printStackTrace();
      }
    }

  }

}
