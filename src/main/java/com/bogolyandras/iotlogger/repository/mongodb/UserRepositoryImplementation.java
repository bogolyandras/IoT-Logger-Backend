package com.bogolyandras.iotlogger.repository.mongodb;

import com.bogolyandras.iotlogger.repository.definition.UserRepository;
import com.bogolyandras.iotlogger.value.account.ApplicationUser;
import com.bogolyandras.iotlogger.value.account.NewAccount;
import com.bogolyandras.iotlogger.value.account.NewAccountWithPasswordHash;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

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

    @Override
    public List<ApplicationUser> getAllUsers() {
        FindIterable<Document> documents = applicationUsers.find();
        MongoCursor<ApplicationUser> iterator = documents.map(this::documentToApplicationUser).iterator();
        List<ApplicationUser> applicationUsers = new ArrayList<>();
        while(iterator.hasNext()) {
            applicationUsers.add(iterator.next());
        }
        return applicationUsers;
    }

    @Override
    public ApplicationUser addAccount(NewAccountWithPasswordHash newAccountWithPasswordHash) {

        NewAccount newAccount = newAccountWithPasswordHash.getNewAccount();

        Document document = new Document("username", newAccount.getUsername())
                .append("password", newAccountWithPasswordHash.getPasswordHash())
                .append("enabled", true)
                .append("firstName", newAccount.getFirstName())
                .append("lastName", newAccount.getLastName())
                .append("userType", newAccount.getUserType().toString())
                .append("registrationTime", new Date());

        applicationUsers.insertOne(document);

        return documentToApplicationUser(document);
    }

    @Override
    public ApplicationUser patchAccount(String identifier, NewAccountWithPasswordHash newAccountWithPasswordHash) {

        NewAccount newAccount = newAccountWithPasswordHash.getNewAccount();

        List<Bson> updateList = Arrays.asList(
                set("username", newAccount.getUsername()),
                set("firstName", newAccount.getFirstName()),
                set("lastName", newAccount.getLastName()),
                set("userType", newAccount.getUserType())
        );
        if (newAccountWithPasswordHash.getPasswordHash() != null) {
            updateList.add(set("password", newAccountWithPasswordHash.getPasswordHash()));
        }

        applicationUsers.updateOne(
                eq("_id", new ObjectId(identifier)),
                combine(updateList)
        );

        return findAccountById(identifier);
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
