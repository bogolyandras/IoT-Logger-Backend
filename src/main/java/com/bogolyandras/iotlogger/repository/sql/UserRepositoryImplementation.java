package com.bogolyandras.iotlogger.repository.sql;

import com.bogolyandras.iotlogger.repository.definition.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
@Profile("default")
public class UserRepositoryImplementation implements UserRepository {

    private DataSource dataSource;

    @Autowired
    public UserRepositoryImplementation(DataSource dataSource) {
        this.dataSource = dataSource;
    }

}
