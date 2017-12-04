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

import java.util.Date;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

@Repository
@Profile("mongodb")
public class InitializationRepositoryImplementation implements InitializationRepository {

    private final MongoDatabase database;
    private final MongoCollection<Document> initialCredentials;
    private final MongoCollection<Document> applicationUsers;
    private final MongoCollection<Document> devices;
    private final MongoCollection<Document> deviceLogs;

    public InitializationRepositoryImplementation(MongoDatabase database) {
        this.database = database;
        this.initialCredentials = database.getCollection("initialCredentials");
        this.applicationUsers = database.getCollection("applicationUsers");
        this.devices = database.getCollection("devices");
        this.deviceLogs = database.getCollection("deviceLogs");
    }

    @Override
    public InitialCredentials getInitialCredentials(String passwordIfNotInitialized) {

        try {

            //Try to find the document
            Document uniqueDocument = initialCredentials.find(
                    eq("_id", "unique_id")
            ).first();

            //If the document found and initialized, return it and exit
            if (uniqueDocument != null) {
                return new InitialCredentials(
                        uniqueDocument.getString("password"),
                        uniqueDocument.getBoolean("initialized")
                );
            }

            //Try to insert a document with unique_id key
            initialCredentials.insertOne(
                    new Document("_id", "unique_id")
                            .append("initialized", false)
                            .append("password", passwordIfNotInitialized)
            );
            //Exception is thrown if that record already exists

            //If we were able to insert the document, we need to initialize the database
            database.createCollection("applicationUsers");
            database.createCollection("devices");
            database.createCollection("deviceLogs");

            applicationUsers.createIndex(Indexes.ascending("username"), new IndexOptions().name("username").unique(true));
            applicationUsers.createIndex(Indexes.ascending("registrationTime"), new IndexOptions().name("registrationTime"));

            devices.createIndex(Indexes.ascending("ownerId"), new IndexOptions().name("ownerId"));

            deviceLogs.createIndex(
                    Indexes.compoundIndex(
                            Indexes.ascending("deviceId"),
                            Indexes.ascending("dataTime")
                    ),
                    new IndexOptions().name("deviceIdThroughDateTime")
            );

            return new InitialCredentials(passwordIfNotInitialized, false);

        } catch (MongoWriteException e) {

            //If it already exists, it will cause a write error, that could come from concurrent initialization
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
                .append("userType", ApplicationUser.UserType.Administrator.toString())
                .append("registrationTime", new Date());
        applicationUsers.insertOne(document);
        return document.getObjectId("_id").toString();
    }

}
