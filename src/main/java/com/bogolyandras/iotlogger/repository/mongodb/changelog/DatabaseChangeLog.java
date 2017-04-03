package com.bogolyandras.iotlogger.repository.mongodb.changelog;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;

@ChangeLog
public class DatabaseChangeLog {

    @ChangeSet(order = "001", id = "someChangeId", author = "andrasboegoely")
    public void initLoad(MongoDatabase db){

        db.createCollection("applicationUsers",
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

        db.getCollection("applicationUsers")
                .createIndex(Indexes.ascending("username"), new IndexOptions().unique(true));

    }

}
