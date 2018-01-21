package com.consulner.service.gios.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.consulner.service.gios.serializers.LocalDateTimeDeserializer;
import com.consulner.service.gios.serializers.LocalDateTimeSerializer;
import java.time.LocalDateTime;

public class SensorValue {

  private LocalDateTime date;
  private Double value;

  @JsonCreator
  public SensorValue(@JsonProperty(value = "date")
//  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
      LocalDateTime date,
      @JsonProperty(value = "value") Double value) {
    this.date = date;
    this.value = value;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public Double getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "SensorValue{" +
        "date='" + date + '\'' +
        ", value=" + value +
        '}';
  }
}
