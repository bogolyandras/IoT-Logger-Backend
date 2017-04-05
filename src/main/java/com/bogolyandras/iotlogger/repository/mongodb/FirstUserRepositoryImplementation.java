package com.bogolyandras.iotlogger.repository.mongodb;

import com.bogolyandras.iotlogger.entity.InitialCredentials;
import com.bogolyandras.iotlogger.repository.definition.FirstUserRepository;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

@Repository
@Profile("mongodb")
public class FirstUserRepositoryImplementation implements FirstUserRepository {

    private MongoCollection<Document> collection;

    @Autowired
    public FirstUserRepositoryImplementation(MongoDatabase mongoDatabase) {
        this.collection = mongoDatabase.getCollection("initialCredentials");
    }

    @Override
    public InitialCredentials getInitialCredentials() {
        Document uniqueDocument = collection.find(eq("uniqueDocument", true))
                .first();
        if (uniqueDocument == null) {
            return null;
        } else {
            return InitialCredentials.builder()
                    .initialized(uniqueDocument.getBoolean("initialized"))
                    .password(uniqueDocument.getString("password"))
                    .build();
        }
    }

    @Override
    public void addInitialCredentials(InitialCredentials initialCredentials) {
        Document document = new Document("uniqueDocument", true)
                .append("initialized", false)
                .append("password", initialCredentials.getPassword());
        collection.insertOne(document);
    }

    @Override
    public void updateInitialCredentials(InitialCredentials initialCredentials) {
        collection.updateOne(
                eq("uniqueDocument", true),
                combine(set("initialized", initialCredentials.getInitialized()), set("password", initialCredentials.getPassword()))
        );
    }


}
