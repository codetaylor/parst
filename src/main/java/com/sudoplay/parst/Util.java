package com.sudoplay.parst;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Util {

  public static void overrideProcessorMap(
      Map<String, LinkedHashMap<String, String>> target,
      Map<String, LinkedHashMap<String, String>> source
  ) {

    if (source == null) {
      return;
    }

    for (Map.Entry<String, LinkedHashMap<String, String>> entry : source.entrySet()) {
      LinkedHashMap<String, String> sourceMap = entry.getValue();
      LinkedHashMap<String, String> targetMap = target.computeIfAbsent(
          entry.getKey(),
          k -> new LinkedHashMap<>()
      );

      for (String key : sourceMap.keySet()) {
        targetMap.put(key, sourceMap.get(key));
      }
    }
  }

  public static Map<String, LinkedHashMap<String, String>> copyProcessorMap(
      Map<String, LinkedHashMap<String, String>> toCopy
  ) {

    Map<String, LinkedHashMap<String, String>> processorMap = new HashMap<>();

    for (Map.Entry<String, LinkedHashMap<String, String>> entry : toCopy.entrySet()) {
      processorMap.put(entry.getKey(), new LinkedHashMap<>(entry.getValue()));
    }

    return processorMap;
  }

}
