package com.sudoplay.parst;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sudoplay.parst.data.ConfigurationData;
import com.sudoplay.parst.data.ExtractFileData;
import com.sudoplay.parst.data.FileData;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {

  public static void main(String[] args) {

    Main app = new Main();
    app.parseCSV(args);
  }

  private void parseCSV(String[] args) {

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

        if (data.extract != null) {
          String targetFolder = data.extract.targetFolder;

          if (targetFolder == null || targetFolder.isEmpty()) {
            targetFolder = data.sourceFolder;
          }

          this.extractCSV(data.extract.sourceFolder, targetFolder, data.extract.files, logger);
        }

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

  private void extractCSV(
      String sourceFolder,
      String targetFolder,
      List<ExtractFileData> files,
      ILogger logger
  ) throws IOException {

    Path targetPath = Paths.get(targetFolder);
    Files.createDirectories(targetPath);
    DataFormatter dataFormatter = new DataFormatter(true);

    for (ExtractFileData fileData : files) {
      Path sourcePath = Paths.get(sourceFolder, fileData.source);

      if (Files.exists(sourcePath)) {

        try {
          this.convertToCSV(sourcePath, targetPath, fileData.target, dataFormatter);

        } catch (InvalidFormatException e) {
          logger.error("Error extracting CSV from: " + sourcePath, e);
        }
      }
    }

  }

  private void convertToCSV(
      Path path,
      Path targetPath,
      String target,
      DataFormatter dataFormatter
  ) throws IOException, InvalidFormatException {

    Workbook sheets = WorkbookFactory.create(path.toFile());
    FormulaEvaluator formulaEvaluator = sheets.getCreationHelper().createFormulaEvaluator();

    for (int sheetIndex = 0; sheetIndex < sheets.getNumberOfSheets(); sheetIndex++) {
      Sheet sheet = sheets.getSheetAt(sheetIndex);
      String sheetName = sheet.getSheetName();
      int maxRowWidth = 0;
      List<List<String>> rowStrings = new ArrayList<>();

      for (int rowIndex = 0; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex++) {
        Row row = sheet.getRow(rowIndex);
        List<String> colStrings = this.rowToCSV(row, dataFormatter, formulaEvaluator);
        rowStrings.add(colStrings);

        if (colStrings.size() > maxRowWidth) {
          maxRowWidth = colStrings.size();
        }
      }

      this.saveSheet(rowStrings, maxRowWidth, sheetName, targetPath, target);
    }

  }

  private void saveSheet(
      List<List<String>> rowStrings,
      int maxRowWidth,
      String sheetName,
      Path targetPath,
      String target
  ) throws IOException {

    Path path = Paths.get(targetPath.toString(), String.format(target, sheetName));

    if (Files.exists(path) && !Files.isDirectory(path)) {
      Files.delete(path);
    }

    if (Files.exists(path)) {
      throw new IOException("Error saving sheet: " + path);
    }

    BufferedWriter writer = null;

    try {

      writer = Files.newBufferedWriter(path);

      for (int i = 0; i < rowStrings.size(); i++) {
        List<String> rowString = rowStrings.get(i);
        StringBuilder builder = new StringBuilder();

        for (int j = 0; j < maxRowWidth; j++) {

          if (rowString.size() > j) {
            String element = rowString.get(j);

            if (element != null) {
              builder.append(this.escapeEmbeddedCharacters(element));
            }
          }

          if (j < maxRowWidth - 1) {
            builder.append(",");
          }
        }

        writer.write(builder.toString().trim());

        if (i < rowStrings.size() - 1) {
          writer.newLine();
        }
      }

    } finally {

      if (writer != null) {
        writer.flush();
        writer.close();
      }
    }
  }

  private ArrayList<String> rowToCSV(
      Row row,
      DataFormatter dataFormatter,
      FormulaEvaluator formulaEvaluator
  ) {

    Cell cell;
    ArrayList<String> result = new ArrayList<>();

    if (row != null) {

      for (int i = 0; i < row.getLastCellNum(); i++) {
        cell = row.getCell(i);

        if (cell == null) {
          result.add("");

        } else {

          if (cell.getCellTypeEnum() != CellType.FORMULA) {
            result.add(dataFormatter.formatCellValue(cell));

          } else {
            result.add(dataFormatter.formatCellValue(cell, formulaEvaluator));
          }
        }
      }
    }

    return result;
  }

  private String escapeEmbeddedCharacters(String field) {

    StringBuilder buffer;

    if (field.contains("\"")) {
      buffer = new StringBuilder(field.replaceAll("\"", "\\\"\\\""));
      buffer.insert(0, "\"");
      buffer.append("\"");

    } else {
      buffer = new StringBuilder(field);

      if ((buffer.indexOf(",")) > -1 ||
          (buffer.indexOf("\n")) > -1) {
        buffer.insert(0, "\"");
        buffer.append("\"");
      }
    }

    return (buffer.toString().trim());
  }
}
