package com.bogolyandras.iotlogger.repository.sql;

import com.bogolyandras.iotlogger.dto.FirstUserCredentials;
import com.bogolyandras.iotlogger.entity.InitialCredentials;
import com.bogolyandras.iotlogger.entity.UserType;
import com.bogolyandras.iotlogger.repository.definition.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;

@Repository
@Profile("default")
public class UserRepositoryImplementation implements UserRepository {

    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryImplementation.class);

    private DataSource dataSource;

    @Autowired
    public UserRepositoryImplementation(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public InitialCredentials getInitialCredentials() {
        try {
            Connection connection = dataSource.getConnection();
            connection.setAutoCommit(true);
            ResultSet resultSet = connection
                    .createStatement()
                    .executeQuery("SELECT `initialized`, `password` FROM `initial_credentials` WHERE `unique_row` = 1");
            if (!resultSet.next()) {
                return null;
            } else {
                return InitialCredentials.builder()
                        .initialized(resultSet.getBoolean("initialized"))
                        .password(resultSet.getString("password"))
                        .build();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addInitialCredentials(InitialCredentials initialCredentials) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(true);
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `initial_credentials`(`unique_row`, `password`, `initialized`) VALUES (?, ?, ?)");
            preparedStatement.setBoolean(1, true);
            preparedStatement.setString(2, initialCredentials.getPassword());
            preparedStatement.setBoolean(3, initialCredentials.getInitialized());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void disableInitialCredentialsAndAddFirstUser(FirstUserCredentials firstUserCredentials) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);

            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `initial_credentials` SET `password`=?,`initialized`=? WHERE `unique_row`=1 AND `password`=?");
            preparedStatement.setString(1, null);
            preparedStatement.setBoolean(2, true);
            preparedStatement.setString(3, firstUserCredentials.getServerPassword());
            int i = preparedStatement.executeUpdate();
            if (i != 1) {
                throw new SQLException("No initial credentials has been updated!");
            }

            PreparedStatement preparedStatementForFirstUserRecord = connection.prepareStatement("INSERT INTO `application_users`(`username`, `password`, `enabled`, `first_name`, `last_name`, `user_type`, `registration_time`) VALUES (?,?,?,?,?,?,?)");
            preparedStatementForFirstUserRecord.setString(1, firstUserCredentials.getUsername());
            preparedStatementForFirstUserRecord.setString(2, firstUserCredentials.getPassword());
            preparedStatementForFirstUserRecord.setBoolean(3, true);
            preparedStatementForFirstUserRecord.setString(4, firstUserCredentials.getFirstName());
            preparedStatementForFirstUserRecord.setString(5, firstUserCredentials.getLastName());
            preparedStatementForFirstUserRecord.setString(6, UserType.Administrator.toString());
            preparedStatementForFirstUserRecord.setTimestamp(7, Timestamp.from(Instant.now()));
            if (preparedStatementForFirstUserRecord.executeUpdate() != 1) {
                throw new SQLException("First user could not be inserted!");
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException e1) {
                throw new RuntimeException(e);
            }
            throw new RuntimeException(e);
        }
    }

}
