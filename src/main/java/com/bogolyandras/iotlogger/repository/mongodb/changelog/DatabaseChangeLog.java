package com.bogolyandras.iotlogger.repository.mongodb.changelog;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;

@ChangeLog
public class DatabaseChangeLog {

    @ChangeSet(order = "001", id = "initializeDatabase", author = "andrasboegoely")
    public void initLoad(MongoDatabase db) {

        db.createCollection("applicationUsers",
                new CreateCollectionOptions().validationOptions(new ValidationOptions()
                        .validator(
                                Filters.and(
                                        Filters.exists("username"),
                                        Filters.exists("password"),
                                        Filters.size("password", 60),
                                        Filters.exists("enabled"),
                                        Filters.exists("firstName"),
                                        Filters.exists("lastName"),
                                        Filters.exists("userType")
                                )
                        ).validationLevel(ValidationLevel.STRICT)
                )
        );

        db.getCollection("applicationUsers")
                .createIndex(Indexes.ascending("username"), new IndexOptions().unique(true));

        db.createCollection("initialCredentials",
                new CreateCollectionOptions().validationOptions(new ValidationOptions()
                        .validator(
                                Filters.and(
                                        Filters.exists("uniqueDocument"),
                                        Filters.exists("password"),
                                        Filters.exists("initialized")
                                )
                        ).validationLevel(ValidationLevel.STRICT)
                )
        );

        db.getCollection("initialCredentials")
                .createIndex(Indexes.ascending("uniqueDocument"), new IndexOptions().unique(true));

    }

}
