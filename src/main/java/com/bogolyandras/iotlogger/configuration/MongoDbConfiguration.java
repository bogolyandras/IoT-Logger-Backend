package com.bogolyandras.iotlogger.configuration;

import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


import java.util.Collections;
import java.util.List;

@Configuration
@Profile("mongodb")
public class MongoDbConfiguration {

    public MongoDbConfiguration(
            @Value("${mongodb.database:#{'iotlogger'}}") String database) {
        this.database = database;
    }

    private final String database;

    @Bean
    public MongoClient mongoClient() {

        /*List<ServerAddress> serverAddresses = Collections.singletonList(
                new ServerAddress("localhost", 27017)
        );

        List<MongoCredential> mongoCredentials = Collections.singletonList(
                MongoCredential.createCredential(userName, database, password.toCharArray())
        );

        MongoClientOptions mongoClientOptions = MongoClientOptions.builder()
                .readConcern(ReadConcern.MAJORITY)
                .writeConcern(WriteConcern.MAJORITY)
                .build();

        return new MongoClient(serverAddresses, mongoCredentials, mongoClientOptions);*/
        return new MongoClient();
    }

    @Bean
    public MongoDatabase mongoDatabase() {
        return mongoClient().getDatabase("iotlogger");
    }

}
