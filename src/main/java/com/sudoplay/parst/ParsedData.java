package com.sudoplay.parst;

import org.apache.commons.csv.CSVRecord;

import java.util.List;
import java.util.Set;

public class ParsedData {

  private final List<String> nameList;
  private final List<String> importList;
  private final Set<String> importSet;
  private final List<CSVRecord> recordList;

  public ParsedData(
      List<String> nameList,
      List<String> importList,
      Set<String> importSet,
      List<CSVRecord> recordList
  ) {

    this.nameList = nameList;
    this.importList = importList;
    this.importSet = importSet;
    this.recordList = recordList;
  }

  public List<String> getNameList() {

    return this.nameList;
  }

  public List<String> getImportList() {

    return this.importList;
  }

  public Set<String> getImportSet() {

    return this.importSet;
  }

  public List<CSVRecord> getRecordList() {

    return this.recordList;
  }
}
