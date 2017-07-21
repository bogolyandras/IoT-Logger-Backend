package com.bogolyandras.iotlogger.repository.mongodb;

import com.bogolyandras.iotlogger.domain.ApplicationUser;
import com.bogolyandras.iotlogger.domain.InitialCredentials;
import com.bogolyandras.iotlogger.domain.UserType;
import com.bogolyandras.iotlogger.dto.FirstUserCredentials;
import com.bogolyandras.iotlogger.repository.definition.UserRepository;
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
public class UserRepositoryImplementation implements UserRepository {

    private MongoCollection<Document> initialCredentials;
    private MongoCollection<Document> applicationUsers;

    public UserRepositoryImplementation(MongoDatabase mongoDatabase) {
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

    @Override
    public ApplicationUser findAccountByUsername(String username) {
        Document document = applicationUsers.find(eq("username", username)).first();
        if (document == null) {
            return null;
        } else {
            return documentToApplicationUser(document);
        }
    }

    @Override
    public ApplicationUser findAccountById(String identifier) {
        Document document = applicationUsers.find(eq("_id", identifier)).first();
        if (document == null) {
            return null;
        } else {
            return documentToApplicationUser(document);
        }
    }

    private ApplicationUser documentToApplicationUser(Document document) {
        ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setId(document.getObjectId("_id").toString());
        applicationUser.setUsername(document.getString("username"));
        applicationUser.setPassword(document.getString("password"));
        applicationUser.setEnabled(document.getBoolean("enabled"));
        applicationUser.setFirstName(document.getString("firstName"));
        applicationUser.setLastName(document.getString("lastName"));
        applicationUser.setUserType(UserType.valueOf(document.getString("userType")));
        applicationUser.setRegistrationTime((long)document.getObjectId("_id").getTimestamp());
        return applicationUser;
    }

}
