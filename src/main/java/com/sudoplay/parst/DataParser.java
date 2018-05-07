package com.sudoplay.parst;

import com.sun.istack.internal.Nullable;
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
    Set<String> importSet = new LinkedHashSet<>();

    for (String name : headerMap.keySet()) {

      if (nameList.contains(name)) {
        throw new Exception("Duplicate name: " + name);
      }

      nameList.add(name);
    }

    CSVRecord metaRecord = recordList.remove(0);
    List<Map<String, String>> metaDataList = new ArrayList<>();

    for (int i = 0; i < nameList.size(); i++) {
      String rawMeta = metaRecord.get(i);
      List<String> metaList = Arrays.asList(rawMeta.split(","));
      Map<String, String> metaMap = new HashMap<>();

      for (String entry : metaList) {
        String[] split = entry.split(":");
        metaMap.put(split[0], split[1]);
      }

      String type = this.locateMetaValue(metaList, "type");

      if (type != null && !type.isEmpty()) {
        importSet.add(type);
      }

      metaDataList.add(metaMap);
    }

    return new ParsedData(nameList, metaDataList, importSet, recordList);
  }

  @Nullable
  private String locateMetaValue(List<String> metaList, String key) {

    key += ":";
    int length = key.length();

    for (String entry : metaList) {
      String trim = entry.trim();

      if (trim.startsWith(key) && trim.length() > length) {
        return trim.substring(length);
      }
    }

    return null;
  }

}
