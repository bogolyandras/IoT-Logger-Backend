package com.bogolyandras.iotlogger.configuration;

import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Configuration
public class MongoDbConfiguration {

    @Autowired
    public MongoDbConfiguration(
            @Value("${mongodb.username:#{null}}") String userName,
            @Value("${mongodb.password:#{null}}") String password,
            @Value("${mongodb.database:#{null}}") String database) {
        this.userName = userName;
        this.password = password;
        this.database = database;
    }

    private String userName;
    private String password;
    private String database;

    @Bean
    public MongoClient mongoClient() {

        List<ServerAddress> serverAddresses = Collections.singletonList(
                new ServerAddress("localhost", 27017)
        );

        List<MongoCredential> mongoCredentials = Collections.singletonList(
                MongoCredential.createCredential(userName, database, password.toCharArray())
        );

        MongoClientOptions mongoClientOptions = MongoClientOptions.builder()
                .readConcern(ReadConcern.MAJORITY)
                .writeConcern(WriteConcern.MAJORITY)
                .build();

        return new MongoClient(serverAddresses, mongoCredentials, mongoClientOptions);
    }

    @Bean
    public MongoDatabase mongoDatabase(MongoClient mongoClient) {
        return mongoClient.getDatabase(database);
    }

}
