package com.sudoplay.parst.data;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedHashMap;
import java.util.Map;

public class FileData {

  public String source;
  public String target;
  public String[] header;

  @SerializedName("processors")
  public Map<String, LinkedHashMap<String, String>> processorMap;

}
