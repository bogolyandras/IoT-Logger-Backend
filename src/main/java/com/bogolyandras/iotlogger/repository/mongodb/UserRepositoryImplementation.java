package com.bogolyandras.iotlogger.repository.mongodb;

import com.bogolyandras.iotlogger.domain.user.ApplicationUser;
import com.bogolyandras.iotlogger.domain.initialize.InitialCredentials;
import com.bogolyandras.iotlogger.domain.user.UserType;
import com.bogolyandras.iotlogger.dto.initialize.FirstUserCredentials;
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

    private final MongoCollection<Document> applicationUsers;

    public UserRepositoryImplementation(MongoDatabase mongoDatabase) {
        this.applicationUsers = mongoDatabase.getCollection("applicationUsers");
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
