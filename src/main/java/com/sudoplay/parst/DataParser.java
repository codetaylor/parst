package com.sudoplay.parst;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataParser {

  public ParsedData parse(Reader reader) throws Exception {

    CSVParser records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);

    Map<String, Integer> headerMap = records.getHeaderMap();
    List<CSVRecord> recordList = records.getRecords();
    List<String> nameList = new ArrayList<>();

    for (String name : headerMap.keySet()) {

      if (nameList.contains(name)) {
        throw new Exception("Duplicate name: " + name);
      }

      nameList.add(name);
    }

    CSVRecord metaRecord = recordList.remove(0);
    List<String> metaDataList = new ArrayList<>();

    for (int i = 0; i < nameList.size(); i++) {
      String rawMeta = metaRecord.get(i);
      metaDataList.add(rawMeta);
    }

    return new ParsedData(nameList, metaDataList, recordList);
  }

}
