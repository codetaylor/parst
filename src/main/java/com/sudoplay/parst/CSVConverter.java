package com.sudoplay.parst;

import com.sudoplay.parst.data.ExtractFileData;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CSVConverter {

  private final ILogger logger;

  public CSVConverter(ILogger logger) {

    this.logger = logger;
  }

  public void extractCSV(
      String sourceFolder,
      String targetFolder,
      List<ExtractFileData> files
  ) throws IOException {

    Path targetPath = Paths.get(targetFolder);
    Files.createDirectories(targetPath);
    DataFormatter dataFormatter = new DataFormatter(true);

    for (ExtractFileData fileData : files) {
      Path sourcePath = Paths.get(sourceFolder, fileData.source);

      if (Files.exists(sourcePath)) {

        try {

          if (Main.USE_ASCII) {
            this.logger.info(String.format("█ [%s]", fileData.source));

          } else {
            this.logger.info(String.format("F [%s]", fileData.source));
          }

          this.convertToCSV(sourcePath, targetPath, fileData.target, dataFormatter);

        } catch (InvalidFormatException e) {
          this.logger.error("Error extracting CSV from: " + sourcePath, e);
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
      boolean isLast = (sheetIndex == sheets.getNumberOfSheets() - 1);

      if (Main.USE_ASCII) {

        if (isLast) {
          this.logger.info(String.format("└─▓ [%d : %s]", sheetIndex, sheetName));

        } else {
          this.logger.info(String.format("├─▓ [%d : %s]", sheetIndex, sheetName));
        }

      } else {
        this.logger.info(String.format("+-S [%d : %s]", sheetIndex, sheetName));
      }

      for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
        Row row = sheet.getRow(rowIndex);
        List<String> colStrings = this.rowToCSV(row, dataFormatter, formulaEvaluator);
        rowStrings.add(colStrings);

        if (colStrings.size() > maxRowWidth) {
          maxRowWidth = colStrings.size();
        }
      }

      this.saveSheet(rowStrings, maxRowWidth, sheetName, targetPath, target, isLast);
    }

  }

  private void saveSheet(
      List<List<String>> rowStrings,
      int maxRowWidth,
      String sheetName,
      Path targetPath,
      String target,
      boolean isLast
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

      if (Main.USE_ASCII) {

        if (isLast) {
          this.logger.info(String.format("  ├─> [%d] lines", rowStrings.size()));
          this.logger.info(String.format("  └─> [%s]", path.toString()));

        } else {
          this.logger.info(String.format("│ ├─> [%d] lines", rowStrings.size()));
          this.logger.info(String.format("│ └─> [%s]", path.toString()));
        }

      } else {

        if (isLast) {
          this.logger.info(String.format("  +-> [%d] lines", rowStrings.size()));
          this.logger.info(String.format("  +-> [%s]", path.toString()));

        } else {
          this.logger.info(String.format("| +-> [%d] lines", rowStrings.size()));
          this.logger.info(String.format("| +-> [%s]", path.toString()));
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
