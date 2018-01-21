package com.consulner.service.stats.model;

public class StatEntry {

  // DynamoDBHashKey
  private String sensorName;
  // DynamoDBRangeKey
  private Long timestamp;
  private Double value;

  public StatEntry(String sensorName, Long timestamp, Double value) {
    this.sensorName = sensorName;
    this.timestamp = timestamp;
    this.value = value;
  }

  public String getSensorName() {
    return sensorName;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public Double getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "StatEntry{" +
        "sensorName='" + sensorName + '\'' +
        ", timestamp=" + timestamp +
        ", value=" + value +
        '}';
  }
}
