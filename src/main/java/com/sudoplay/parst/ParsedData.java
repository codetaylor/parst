package com.sudoplay.parst;

import org.apache.commons.csv.CSVRecord;

import java.util.List;

public class ParsedData {

  private final List<String> nameList;
  private List<String> metaDataList;
  private final List<CSVRecord> recordList;

  public ParsedData(
      List<String> nameList,
      List<String> metaDataList,
      List<CSVRecord> recordList
  ) {

    this.nameList = nameList;
    this.metaDataList = metaDataList;
    this.recordList = recordList;
  }

  public List<String> getNameList() {

    return this.nameList;
  }

  public List<String> getMetaDataList() {

    return this.metaDataList;
  }

  public List<CSVRecord> getRecordList() {

    return this.recordList;
  }
}
