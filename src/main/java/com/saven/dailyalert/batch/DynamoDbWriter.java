package com.saven.dailyalert.batch;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.saven.dailyalert.domain.Column;
import com.saven.dailyalert.domain.Row;

import org.springframework.batch.item.ItemWriter;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DynamoDbWriter implements ItemWriter {

    Logger logger = Logger.getLogger(DynamoDbWriter.class.getName());

    private String region;

    private String accessKey;

    private String secretKey;

    private String tableName;

    private String hashKeyPrefix;

    private String hashKeySuffix;

    private String hashKeyName;

    private String rangeKeyName;

    private boolean skipWrite;

    @Override
    public void write(List list) throws Exception {
        DynamoDB dynamoDB = establishConnection();
        Table t = dynamoDB.getTable(tableName);
        for (Object obj : list) {
            Row row = (Row) obj;
            logger.info(row.toString());
            Map<String, Object> map = row.getColumns().stream().collect(Collectors.toMap(Column::getName, Column::getValue));
            map.remove(rangeKeyName);
            String hashKeyValue = hashKeyPrefix + row.getColumn(row.getRowIdColumn()).get().getValue() + hashKeySuffix;
            String rangeKeyValue = row.getColumn(rangeKeyName).get().getValue();
            logger.info("Item Primary Key : " + hashKeyValue + " Sort Key: " + rangeKeyValue);
            Item item = new Item().fromMap(map).withPrimaryKey(hashKeyName, Long.valueOf(hashKeyValue), rangeKeyName, Long.valueOf(rangeKeyValue));

            if(!skipWrite) {
                t.putItem(item);
            } else {
                logger.info(item.toJSONPretty());
            }
        }
    }

    public DynamoDB establishConnection() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);
        AmazonDynamoDBClient client = (AmazonDynamoDBClient) AmazonDynamoDBClientBuilder.standard().withCredentials(credentialsProvider).withRegion(region).build();
        DynamoDB dynamoDB = new DynamoDB(client);
        logger.info("Connection established.");
        return dynamoDB;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getHashKeyPrefix() {
        return hashKeyPrefix;
    }

    public void setHashKeyPrefix(String hashKeyPrefix) {
        this.hashKeyPrefix = hashKeyPrefix;
    }

    public String getHashKeyName() {
        return hashKeyName;
    }

    public void setHashKeyName(String hashKeyName) {
        this.hashKeyName = hashKeyName;
    }

    public String getRangeKeyName() {
        return rangeKeyName;
    }

    public void setRangeKeyName(String rangeKeyName) {
        this.rangeKeyName = rangeKeyName;
    }

    public String getHashKeySuffix() {
        return hashKeySuffix;
    }

    public void setHashKeySuffix(String hashKeySuffix) {
        this.hashKeySuffix = hashKeySuffix;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public boolean isSkipWrite() {
        return skipWrite;
    }

    public void setSkipWrite(boolean skipWrite) {
        this.skipWrite = skipWrite;
    }
}
