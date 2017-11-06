package com.bogolyandras.iotlogger.repository.mongodb;

import com.bogolyandras.iotlogger.repository.definition.InitializationRepository;
import com.bogolyandras.iotlogger.value.account.ApplicationUser;
import com.bogolyandras.iotlogger.value.initialize.FirstUserCredentials;
import com.bogolyandras.iotlogger.value.initialize.FirstUserCredentialsWithPasswordHash;
import com.bogolyandras.iotlogger.value.initialize.InitialCredentials;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Repository;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

@Repository
@Profile("mongodb")
public class InitializationRepositoryImplementation implements InitializationRepository {

    private final MongoCollection<Document> initialCredentials;
    private final MongoCollection<Document> applicationUsers;
    private final MongoCollection<Document> devices;
    private final MongoCollection<Document> deviceLogs;

    public InitializationRepositoryImplementation(MongoDatabase mongoDatabase) {
        this.initialCredentials = mongoDatabase.getCollection("initialCredentials");
        this.applicationUsers = mongoDatabase.getCollection("applicationUsers");
        this.devices = mongoDatabase.getCollection("devices");
        this.deviceLogs = mongoDatabase.getCollection("deviceLogs");
    }

    @Override
    public InitialCredentials getInitialCredentials(String passwordIfNotInitialized) {

        try {

            //Try to insert a document with unique_id key
            initialCredentials.insertOne(
                    new Document("_id", "unique_id")
                            .append("initialized", false)
                            .append("password", passwordIfNotInitialized)
            );

            applicationUsers.createIndex(Indexes.ascending("username"), new IndexOptions().unique(true));
            applicationUsers.createIndex(Indexes.ascending("registrationTime"));

            devices.createIndex(Indexes.ascending("ownerId"));

            deviceLogs.createIndex(Indexes.compoundIndex(Indexes.ascending("deviceId"), Indexes.ascending("dataTime")));

            return new InitialCredentials(passwordIfNotInitialized, false);

        } catch (MongoWriteException e) {

            //If it already exists, it will cause a write error
            Document uniqueDocument = initialCredentials.find(
                    eq("_id", "unique_id")
            ).first();

            return new InitialCredentials(
                    uniqueDocument.getString("password"),
                    uniqueDocument.getBoolean("initialized")
            );

        } catch (MongoException e) {
            //If there is a database connectivity problem, we can't do nothing, let's retry later
            return null;
        }

    }

    @Override
    public String disableInitialCredentialsAndAddFirstUser(FirstUserCredentialsWithPasswordHash firstUserCredentialsWithPasswordHash) {

        FirstUserCredentials firstUserCredentials = firstUserCredentialsWithPasswordHash.getFirstUserCredentials();

        UpdateResult updateResult = initialCredentials.updateOne(
                and(eq("initialized", false), eq("password", firstUserCredentials.getServerPassword())),
                combine(set("initialized", true), set("password", null))
        );
        if (updateResult.getModifiedCount() != 1) {
            throw new BadCredentialsException("Failed to update initial credentials!");
        }

        Document document = new Document("username", firstUserCredentials.getUsername())
                .append("password", firstUserCredentialsWithPasswordHash.getPasswordHash())
                .append("enabled", true)
                .append("firstName", firstUserCredentials.getFirstName())
                .append("lastName", firstUserCredentials.getLastName())
                .append("userType", ApplicationUser.UserType.Administrator.toString());
        applicationUsers.insertOne(document);
        return document.getObjectId("_id").toString();
    }

}
