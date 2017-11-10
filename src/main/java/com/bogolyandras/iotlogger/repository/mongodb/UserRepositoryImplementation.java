package com.bogolyandras.iotlogger.repository.mongodb;

import com.bogolyandras.iotlogger.repository.definition.UserRepository;
import com.bogolyandras.iotlogger.value.account.ApplicationUser;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import static com.mongodb.client.model.Filters.eq;

@Repository
@Profile("mongodb")
public class UserRepositoryImplementation implements UserRepository {

    private final MongoCollection<Document> applicationUsers;

    public UserRepositoryImplementation(MongoDatabase database) {
        this.applicationUsers = database.getCollection("applicationUsers");
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
        Document document = applicationUsers.find(eq("_id", new ObjectId(identifier))).first();
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
        applicationUser.setUserType(ApplicationUser.UserType.valueOf(document.getString("userType")));
        applicationUser.setRegistrationTime(document.getDate("registrationTime").toInstant());
        return applicationUser;
    }

}
