package com.consulner.service.gios.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * "param": {
 * "paramName": "tlenek węgla",
 * "paramFormula": "CO",
 * "paramCode": "CO",
 * "idParam": 8
 * },
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Param {

  private int idParam;
  private String paramFormula;
  private String paramName;

}
