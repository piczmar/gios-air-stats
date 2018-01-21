package com.consulner.service.gios.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * {
 * "id": 16228,
 * "stationId": 10058,
 * "param": {
 * "paramName": "tlenek węgla",
 * "paramFormula": "CO",
 * "paramCode": "CO",
 * "idParam": 8
 * },
 * "sensorDateStart": "2015-11-27 00:00:00",
 * "sensorDateEnd": null
 * }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sensor {

  private int id;

  private Param param;

  public Sensor(@JsonProperty(value = "id") int id, @JsonProperty(value = "param") Param param) {
    this.id = id;
    this.param = param;
  }

  public int getId() {
    return id;
  }

  public Param getParam() {
    return param;
  }
}
