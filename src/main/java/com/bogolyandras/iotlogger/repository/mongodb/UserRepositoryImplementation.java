package com.bogolyandras.iotlogger.repository.mongodb;

import com.bogolyandras.iotlogger.dto.FirstUserCredentials;
import com.bogolyandras.iotlogger.entity.ApplicationUser;
import com.bogolyandras.iotlogger.entity.InitialCredentials;
import com.bogolyandras.iotlogger.entity.UserType;
import com.bogolyandras.iotlogger.repository.definition.UserRepository;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

@Repository
@Profile("mongodb")
public class UserRepositoryImplementation implements UserRepository {

    private MongoCollection<Document> initialCredentials;
    private MongoCollection<Document> applicationUsers;

    @Autowired
    public UserRepositoryImplementation(MongoDatabase mongoDatabase) {
        this.initialCredentials = mongoDatabase.getCollection("initialCredentials");
        this.applicationUsers = mongoDatabase.getCollection("applicationUsers");
    }

    @Override
    public InitialCredentials getInitialCredentials() {
        Document uniqueDocument = initialCredentials.find(eq("uniqueDocument", true))
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
        this.initialCredentials.insertOne(document);
    }

    @Override
    public void disableInitialCredentialsAndAddFirstUser(FirstUserCredentials firstUserCredentials) {

        UpdateResult updateResult = initialCredentials.updateOne(
                and(eq("uniqueDocument", true), eq("initialized", false), eq("password", firstUserCredentials.getServerPassword())),
                combine(set("initialized", true), set("password", null))
        );
        if (updateResult.getModifiedCount() != 1) {
            throw new RuntimeException("Initial Credentials has not been updated!");
        }

        Document document = new Document("username", firstUserCredentials.getUsername())
                .append("password", firstUserCredentials.getPassword())
                .append("enabled", true)
                .append("firstName", firstUserCredentials.getFirstName())
                .append("lastName", firstUserCredentials.getLastName())
                .append("userType", UserType.Administrator.toString());
        applicationUsers.insertOne(document);
    }

    @Override
    public ApplicationUser findAccountByUsername(String username) {
        throw new RuntimeException("Not implemented yet!");
    }

    @Override
    public ApplicationUser findAccountById(String identifier) {
        throw new RuntimeException("Not implemented yet!");
    }

}
