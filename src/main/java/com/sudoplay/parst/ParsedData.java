package com.sudoplay.parst;

import org.apache.commons.csv.CSVRecord;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParsedData {

  private final List<String> nameList;
  private List<Map<String, String>> metaDataList;
  private final Set<String> importSet;
  private final List<CSVRecord> recordList;

  public ParsedData(
      List<String> nameList,
      List<Map<String, String>> metaDataList,
      Set<String> importSet,
      List<CSVRecord> recordList
  ) {

    this.nameList = nameList;
    this.metaDataList = metaDataList;
    this.importSet = importSet;
    this.recordList = recordList;
  }

  public List<String> getNameList() {

    return this.nameList;
  }

  public List<Map<String, String>> getMetaDataList() {

    return this.metaDataList;
  }

  public Set<String> getImportSet() {

    return this.importSet;
  }

  public List<CSVRecord> getRecordList() {

    return this.recordList;
  }
}
