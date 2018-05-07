package com.sudoplay.parst;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.Reader;
import java.util.*;

public class DataParser {

  public ParsedData parse(Reader reader) throws Exception {

    CSVParser records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);

    Map<String, Integer> headerMap = records.getHeaderMap();
    List<CSVRecord> recordList = records.getRecords();
    List<String> nameList = new ArrayList<>();
    List<String> importList = new ArrayList<>();
    Set<String> importSet = new LinkedHashSet<>();

    for (String s : headerMap.keySet()) {

      String[] split = s.split(":");

      if (nameList.contains(split[0])) {
        throw new Exception("Duplicate name: " + split[0]);
      }

      nameList.add(split[0]);
      importList.add(split[1]);
      importSet.add(split[1]);
    }

    return new ParsedData(nameList, importList, importSet, recordList);
  }

}
