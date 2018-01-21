package com.consulner.service.stats.dao;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import com.consulner.Handler;
import com.consulner.service.stats.model.StatEntry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.apache.log4j.Logger;

public class StatEntryDao {

  private static final Logger LOG = Logger.getLogger(Handler.class);
  private static final String KEY_ATTRIBUTE_NAME = "sensorName";
  private final String tableName;

  public StatEntryDao(String tableName) {
    this.tableName = tableName;
  }

  /**
   * Returns an entry.
   *
   * @param timestamp entry timestamp
   * @return the Optional of the StatEntry.
   */
  public Optional<StatEntry> get(Long timestamp) {
    GetItemSpec spec = new GetItemSpec()
        .withPrimaryKey(KEY_ATTRIBUTE_NAME, timestamp)
        .withProjectionExpression(KEY_ATTRIBUTE_NAME + ",timestamp,value")
        .withConsistentRead(true);

    try {
      Item item = makeDynamo().getTable(tableName).getItem(spec);
      return Optional.ofNullable(toStatEntry(item));
    } catch (RuntimeException ex) {
      LOG.warn(String.format("Error while finding entry '%s'", timestamp), ex);
      throw ex;
    }
  }

  /**
   * Saves an entry.
   *
   * @param entry to be saved.
   */
  public void put(StatEntry entry) {
    LOG.debug(String.format("Attempting to save entry '%s'", entry));
    try {
      makeDynamo().getTable(tableName).putItem(toDynamoItem(entry));
      LOG.info(String.format("Entry '%s' saved successfully", entry));
    } catch (RuntimeException ex) {
      LOG.warn(String.format("Error when saving entry '%s'", entry), ex);
      throw new RuntimeException(ex);
    }
  }

  /**
   * Returns the list of all entries.
   *
   * @return the list of {@link StatEntry} objects.
   */
  public List<StatEntry> getAllInTimeRange(String sensorName, Long start, Long end) {
    List<StatEntry> output = new ArrayList<>();
    QuerySpec spec = new QuerySpec().withProjectionExpression("sensorName, timestamp, value")
        .withKeyConditionExpression(
            "sensorName = :sensorName and timestamp between :start and :end")
        .withValueMap(new ValueMap()
            .withString(":sensorName", sensorName)
            .withLong(":start", start)
            .withLong(":end", end));

    ItemCollection<QueryOutcome> items = makeDynamo().getTable(tableName).query(spec);

    Iterator<Item> iterator = items.iterator();
    while (iterator.hasNext()) {
      Item item = iterator.next();
      output.add(toStatEntry(item));
    }
    return output;
  }

  private Item toDynamoItem(StatEntry entry) {
    return new Item()
        .withString(KEY_ATTRIBUTE_NAME, entry.getSensorName())
        .withLong("timestamp", entry.getTimestamp())
        .withDouble("value", entry.getValue());
  }

  private StatEntry toStatEntry(Item item) {
    if (item == null) {
      return null;
    }
    return new StatEntry(item.getString(KEY_ATTRIBUTE_NAME), item.getLong("timestamp"),
        item.getDouble("value"));
  }

  private DynamoDB makeDynamo() {
    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
//        .withCredentials(new ProfileCredentialsProvider("sls"))
        .withRegion(Regions.US_EAST_1)
        .build();
    return new DynamoDB(client);
  }

}
