package com.bogolyandras.iotlogger.repository.mongodb;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Profile("mongodb")
public class DatabaseInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    private MongoDatabase mongoDatabase;

    @Autowired
    public DatabaseInitializer(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
        initializeDatabase();
    }

    private void initializeDatabase() {

        if (mongoDatabase.listCollectionNames().into(new ArrayList<>()).contains("databaseChangeLog")) {
            logger.info("Database has been already initialized, resuming application...");
            return;
        }

        logger.info("Initializing database...");

        mongoDatabase.createCollection("databaseChangeLog");

        mongoDatabase.createCollection("applicationUsers",
                new CreateCollectionOptions().validationOptions(new ValidationOptions()
                    .validator(
                        Filters.and(
                                Filters.exists("username"),
                                Filters.exists("password"),
                                Filters.size("password", 60),
                                Filters.exists("firstName"),
                                Filters.exists("lastName")
                        )
                    ).validationLevel(ValidationLevel.STRICT)
                )
        );

        mongoDatabase.getCollection("applicationUsers")
                .createIndex(Indexes.ascending("username"), new IndexOptions().unique(true));

    }
}
