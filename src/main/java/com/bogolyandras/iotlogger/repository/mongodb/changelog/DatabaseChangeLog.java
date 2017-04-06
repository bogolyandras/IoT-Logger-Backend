package com.bogolyandras.iotlogger.repository.mongodb.changelog;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;

import static com.mongodb.client.model.Filters.*;

@ChangeLog
public class DatabaseChangeLog {

    @ChangeSet(order = "001", id = "initializeDatabase", author = "andrasboegoely")
    public void initLoad(MongoDatabase db) {

        db.createCollection("applicationUsers",
                new CreateCollectionOptions().validationOptions(new ValidationOptions()
                        .validator(
                                and(
                                        exists("username"),
                                        exists("password"),
                                        exists("enabled"),
                                        exists("firstName"),
                                        exists("lastName"),
                                        exists("userType")
                                )
                        ).validationLevel(ValidationLevel.STRICT)
                )
        );

        db.getCollection("applicationUsers")
                .createIndex(Indexes.ascending("username"), new IndexOptions().unique(true));

        db.createCollection("initialCredentials",
                new CreateCollectionOptions().validationOptions(new ValidationOptions()
                        .validator(
                                and(
                                        exists("uniqueDocument"),
                                        exists("password"),
                                        exists("initialized")
                                )
                        ).validationLevel(ValidationLevel.STRICT)
                )
        );

        db.getCollection("initialCredentials")
                .createIndex(Indexes.ascending("uniqueDocument"), new IndexOptions().unique(true));

    }

}
