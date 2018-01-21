package com.consulner.service.gios.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SensorData {

  private String key;
  private List<SensorValue> values;

  @JsonCreator
  public SensorData(@JsonProperty(value = "key") String key,
      @JsonProperty(value = "values", required = true)
          List<SensorValue> values) {
    this.key = key;
    this.values = values;
  }

  public String getKey() {
    return key;
  }

  public List<SensorValue> getValues() {
    return values;
  }

  @Override
  public String toString() {
    return "SensorData{" +
        "key='" + key + '\'' +
        ", values=" + values +
        '}';
  }

}
