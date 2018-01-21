package com.consulner.service.stats;

import com.consulner.service.gios.GiosService;
import com.consulner.service.gios.model.Sensor;
import com.consulner.service.gios.model.SensorData;
import com.consulner.service.gios.model.SensorValue;
import com.consulner.service.stats.dao.StatEntryDao;
import com.consulner.service.stats.model.StatEntry;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;

public class StatsService {
  private static final Logger LOG = Logger.getLogger(StatsService.class);
  private static final int STATION_ID_LODZ = 10058;
  private static final String SERVICE_NAME = "gios-air-stats";
  public static final String STAGE = "dev";

  private GiosService giosService = new GiosService();
  private StatEntryDao statEntryDao = new StatEntryDao(
      SERVICE_NAME + "-" + STAGE + "-" + "data"); //TODO: this needs to be parametrized based on consulner.yml

  public void updateStats() {
    // get last entry in database
    Long lastDateMillis = 0L;

    // get delta from now and last entry
    Long nowMillis = new Date().getTime();

    Long delta = nowMillis - lastDateMillis;

    ZoneId zoneId = ZoneId.of("Europe/Paris");

    GiosService service = new GiosService();
    Optional<List<Sensor>> sensors = service.getSensors(STATION_ID_LODZ);

    List<Integer> sensorIds = sensors
        .map(ss ->  ss.stream().map(Sensor::getId).collect(Collectors.toList()))
        .orElse(Collections.emptyList());

    LOG.info("sensor ids: " + sensorIds);

    LocalDateTime afterDate = LocalDateTime.now().minus(1, ChronoUnit.DAYS);

    sensorIds.parallelStream().forEach(sensorId -> {
      SensorData data = service.getSensorData(sensorId, Optional.of(afterDate)).get();
      Optional<SensorValue> max = data.getValues().stream()
          .max(Comparator.comparing(SensorValue::getDate));
      if (max.isPresent()) {
        SensorValue value = max.get();
        LOG.info("Saving sensor: " + data.getKey() + ", at " + value.getDate() + " , value: " + value.getValue());
        statEntryDao.put(
            new StatEntry(data.getKey(), value.getDate().atZone(zoneId).toEpochSecond(),
                value.getValue()));
      }
    });

  }

  public static void main(String[] args) {
    StatsService service = new StatsService();
//    service.statEntryDao.put(new StatEntry("test", System.currentTimeMillis(), 11.1));

//    service.updateStats();
  }

}
