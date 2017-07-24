package com.bogolyandras.iotlogger.repository.mongodb;

import com.bogolyandras.iotlogger.domain.initialize.InitialCredentials;
import com.bogolyandras.iotlogger.domain.user.UserType;
import com.bogolyandras.iotlogger.dto.initialize.FirstUserCredentials;
import com.bogolyandras.iotlogger.repository.definition.InitializationRepository;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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
@Profile("default")
public class InitializationRepositoryImplementation implements InitializationRepository {

    private final MongoCollection<Document> initialCredentials;
    private final MongoCollection<Document> applicationUsers;

    public InitializationRepositoryImplementation(MongoDatabase mongoDatabase) {
        this.initialCredentials = mongoDatabase.getCollection("initialCredentials");
        this.applicationUsers = mongoDatabase.getCollection("applicationUsers");
    }

    @Override
    public InitialCredentials getInitialCredentials(String passwordIfNotInitialized) {

        try {

            initialCredentials.insertOne(
                    new Document("_id", "uniqueId")
                            .append("initialized", false)
                            .append("password", passwordIfNotInitialized)
            );

            return new InitialCredentials(passwordIfNotInitialized, false);

        } catch (MongoWriteException e) {

            Document uniqueDocument = initialCredentials.find(
                    eq("_id", "uniqueId")
            ).first();

            return new InitialCredentials(
                    uniqueDocument.getString("password"),
                    uniqueDocument.getBoolean("initialized")
            );

        }

    }

    @Override
    public String disableInitialCredentialsAndAddFirstUser(FirstUserCredentials firstUserCredentials) {

        UpdateResult updateResult = initialCredentials.updateOne(
                and(eq("initialized", false), eq("password", firstUserCredentials.getServerPassword())),
                combine(set("initialized", true), set("password", null))
        );
        if (updateResult.getModifiedCount() != 1) {
            throw new BadCredentialsException("Failed to update initial credentials!");
        }

        Document document = new Document("username", firstUserCredentials.getUsername())
                .append("password", firstUserCredentials.getPassword())
                .append("enabled", true)
                .append("firstName", firstUserCredentials.getFirstName())
                .append("lastName", firstUserCredentials.getLastName())
                .append("userType", UserType.Administrator.toString());
        applicationUsers.insertOne(document);
        return document.getObjectId("_id").toString();
    }

}
