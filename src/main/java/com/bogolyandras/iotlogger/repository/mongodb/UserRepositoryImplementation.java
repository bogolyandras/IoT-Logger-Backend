package com.bogolyandras.iotlogger.repository.mongodb;

import com.bogolyandras.iotlogger.repository.definition.UserRepository;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("mongodb")
public class UserRepositoryImplementation implements UserRepository {

    private MongoDatabase mongoDatabase;

    @Autowired
    public UserRepositoryImplementation(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

}
