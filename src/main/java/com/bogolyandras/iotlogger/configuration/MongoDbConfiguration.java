package com.bogolyandras.iotlogger.configuration;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Profile("mongodb")
@ConfigurationProperties(prefix="mongodb")
public class MongoDbConfiguration {

    //mongodb.servers[]
    private final List<String> servers = new ArrayList<>();
    public List<String> getServers() {
        return servers;
    }

    //mongodb.serverPorts[]
    private final List<Integer> serverPorts = new ArrayList<>();
    public List<Integer> getServerPorts() {
        return serverPorts;
    }

    public MongoDbConfiguration(
            @Value("${mongodb.database:#{'iotlogger'}}") String database) {
        this.database = database;
    }

    private final String database;

    @Bean
    public MongoClient mongoClient() {

        MongoClientOptions options = MongoClientOptions.builder().readPreference(
                ReadPreference.nearest()).build();

        if (servers.size() != serverPorts.size()) {
            throw new RuntimeException("Configuration error, you must provide server addresses and ports as well, the same amount!");
        }

        if (servers.size() > 0) {
            List<ServerAddress> serverAddresses = new ArrayList<>();
            for (int i = 0; i < servers.size(); i++) {
                serverAddresses.add(new ServerAddress(servers.get(i), serverPorts.get(i)));
            }
            return new MongoClient(
                    serverAddresses,
                    options
            );
        } else {
            //Default mongoclient
            return new MongoClient();
        }

    }

    @Bean
    public MongoDatabase mongoDatabase() {
        return mongoClient().getDatabase(database);
    }

}
