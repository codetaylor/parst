package com.sudoplay.parst;

import com.google.gson.Gson;
import com.sudoplay.parst.data.ConfigurationData;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigurationDataLoader {

  private final Gson gson;

  public ConfigurationDataLoader(Gson gson) {

    this.gson = gson;
  }

  public ConfigurationData load(String location) throws IOException {

    Path path = Paths.get(location);

    if (!Files.exists(path)) {
      throw new IOException("File doesn't exist: " + path);
    }

    BufferedReader bufferedReader = null;

    try {
      bufferedReader = Files.newBufferedReader(path);
      return this.gson.fromJson(bufferedReader, ConfigurationData.class);

    } finally {

      if (bufferedReader != null) {

        try {
          bufferedReader.close();

        } catch (IOException e) {
          //
        }
      }
    }
  }

}
