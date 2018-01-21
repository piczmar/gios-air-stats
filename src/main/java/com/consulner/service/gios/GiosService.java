package com.consulner.service.gios;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.consulner.service.gios.model.Sensor;
import com.consulner.service.gios.model.SensorData;
import com.consulner.service.gios.model.SensorValue;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.javalite.http.Get;
import org.javalite.http.Http;

/**
 * Uses GIOS REST API to get air condition information from
 *
 * - get all stations:        http://api.gios.gov.pl/pjp-api/rest/station/findAll
 * - get sensors in station:  http://api.gios.gov.pl/pjp-api/rest/station/sensors/10058
 * - get reads from sensor:   http://api.gios.gov.pl/pjp-api/rest/data/getData/16228
 */
public class GiosService {

  private static final String API_GET_SENSORS_FOR_STATION = "http://api.gios.gov.pl/pjp-api/rest/station/sensors/";
  private static final String API_GET_SENSOR_DATA = "http://api.gios.gov.pl/pjp-api/rest/data/getData/";
  private static final Logger LOG = Logger.getLogger(GiosService.class);

  public Optional<List<Sensor>> getSensors(int stationId) {
    List<Sensor> response = null;
    try {
      Get sensorResponse = Http.get(API_GET_SENSORS_FOR_STATION + stationId);

      response = this.jsonArrayToObjectList(sensorResponse.text(), Sensor.class);
    } catch (Exception e) {
      e.printStackTrace();
      LOG.error("Cannot get sensors from station " + stationId, e);
    }
    return Optional.ofNullable(response);
  }

  public Optional<SensorData> getSensorData(int sensorId, Optional<LocalDateTime> afterDate) {
    SensorData response = null;
    try {
      Get data = Http.get(API_GET_SENSOR_DATA + sensorId);

      ObjectMapper objectMapper = new ObjectMapper();

      String text = data.text();
      response = objectMapper.readValue(text, SensorData.class);

      if (response != null && afterDate.isPresent()) {
        List<SensorValue> valuesAfterDate = response.getValues().stream()
            .filter(new SensorValueAfterDate(afterDate.get())).collect(Collectors.toList());
        return Optional.of(new SensorData(response.getKey(), valuesAfterDate));
      }

    } catch (Exception e) {
      e.printStackTrace();
      LOG.error("Cannot get sensor data for sensor " + sensorId, e);
    }
    return Optional.ofNullable(response);
  }

  private <T> List<T> jsonArrayToObjectList(String json, Class<T> tClass) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    CollectionType listType = mapper.getTypeFactory()
        .constructCollectionType(ArrayList.class, tClass);
    List<T> ts = mapper.readValue(json, listType);
    return ts;
  }

  private class SensorValueAfterDate implements java.util.function.Predicate<SensorValue> {

    private LocalDateTime afterDate;

    public SensorValueAfterDate(LocalDateTime afterDate) {
      this.afterDate = afterDate;
    }

    @Override
    public boolean test(SensorValue v) {
      return v.getDate().isAfter(afterDate);
    }
  }


  public static void main(String[] args) throws ParseException {
    LocalDateTime afterDate = LocalDateTime
        .parse("2018-01-14 18:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    GiosService service = new GiosService();
    Optional<List<Sensor>> sensors = service.getSensors(10058);

    List<Integer> sensorIds = sensors
        .map(ss -> {
          System.out.println(ss);
          return ss.stream().map(Sensor::getId).collect(Collectors.toList());
        })
        .orElse(Collections.emptyList());

    System.out.println(sensorIds);

    long start = System.currentTimeMillis();
    sensorIds.parallelStream().forEach(sensorId -> {
      SensorData data = service.getSensorData(sensorId, Optional.of(afterDate)).get();
      System.out.println("afterDate: " + data);
      Optional<SensorValue> max = data.getValues().stream()
          .max(Comparator.comparing(SensorValue::getDate));
      if (max.isPresent()) {
        System.out.println("max = " + max.get());
      }
    });
    System.out.println("Total time [ms]: " + (System.currentTimeMillis() - start));
  }

}
