package com.bogolyandras.iotlogger.repository.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("mongodb")
public class DeviceLogRepositoryImplementation {

    private final MongoCollection<Document> devices;
    private final MongoCollection<Document> deviceLogs;

    public DeviceLogRepositoryImplementation(MongoDatabase database) {
        devices = database.getCollection("devices");
        deviceLogs = database.getCollection("deviceLogs");
    }

}
