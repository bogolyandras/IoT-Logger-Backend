package com.bogolyandras.iotlogger.repository.mongodb;

import com.bogolyandras.iotlogger.repository.definition.DeviceRepository;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("mongodb")
public class DeviceRepositoryImplementation implements DeviceRepository {

    private final MongoCollection<Document> applicationUsers;
    private final MongoCollection<Document> devices;

    public DeviceRepositoryImplementation(MongoDatabase database) {
        this.applicationUsers = database.getCollection("applicationUsers");
        this.devices = database.getCollection("devices");
    }


}
