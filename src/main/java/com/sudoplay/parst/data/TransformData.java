package com.sudoplay.parst.data;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TransformData {

  public String sourceFolder = "";

  public String targetFolder = "";

  public String processorFolder = "";

  @SerializedName("processors")
  public Map<String, LinkedHashMap<String, String>> processorMap;

  @SerializedName("files")
  public List<FileData> fileDataList;

}
