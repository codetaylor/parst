package com.sudoplay.parst.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class ConfigurationData {

  @SerializedName("imports")
  public Map<String, String> importMap;

  public String sourceFolder = "";

  public String targetFolder = "";

  @SerializedName("processors")
  public Map<String, Map<String, String>> processorMap;

  @SerializedName("files")
  public List<FileData> fileDataList;

}
