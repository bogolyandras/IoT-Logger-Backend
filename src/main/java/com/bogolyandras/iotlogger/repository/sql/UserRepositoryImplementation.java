package com.bogolyandras.iotlogger.repository.sql;

import com.bogolyandras.iotlogger.domain.ApplicationUser;
import com.bogolyandras.iotlogger.domain.InitialCredentials;
import com.bogolyandras.iotlogger.domain.UserType;
import com.bogolyandras.iotlogger.dto.FirstUserCredentials;
import com.bogolyandras.iotlogger.repository.definition.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
@Profile("mysql")
public class UserRepositoryImplementation implements UserRepository {

    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryImplementation.class);

    private final Connection connection;

    public UserRepositoryImplementation(Connection connection) {
        this.connection = connection;
    }

    @Override
    public InitialCredentials getInitialCredentials() {
        try {
            connection.setAutoCommit(true);
            ResultSet resultSet = connection
                    .createStatement()
                    .executeQuery("SELECT `initialized`, `password` FROM `initial_credentials` WHERE `unique_row` = 1");
            if (!resultSet.next()) {
                return null;
            } else {
                return new InitialCredentials(
                        resultSet.getString("password"),
                        resultSet.getBoolean("initialized")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addInitialCredentials(InitialCredentials initialCredentials) {
        Connection connection = null;
        try {
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
    public String disableInitialCredentialsAndAddFirstUser(FirstUserCredentials firstUserCredentials) {
        Connection connection = null;
        try {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);

            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `initial_credentials` SET `password`=?,`initialized`=? WHERE `unique_row`=1 AND `password`=?");
            preparedStatement.setString(1, null);
            preparedStatement.setBoolean(2, true);
            preparedStatement.setString(3, firstUserCredentials.getServerPassword());
            int i = preparedStatement.executeUpdate();
            if (i != 1) {
                throw new SQLException("Initial Credentials has not been updated!");
            }

            PreparedStatement preparedStatementForFirstUserRecord = connection.prepareStatement(
                    "INSERT INTO `application_users`(`username`, `password`, `enabled`, `first_name`, `last_name`, `user_type`, `registration_time`) " +
                            "VALUES (?,?,?,?,?,?,NOW())",
                    Statement.RETURN_GENERATED_KEYS);
            preparedStatementForFirstUserRecord.setString(1, firstUserCredentials.getUsername());
            preparedStatementForFirstUserRecord.setString(2, firstUserCredentials.getPassword());
            preparedStatementForFirstUserRecord.setBoolean(3, true);
            preparedStatementForFirstUserRecord.setString(4, firstUserCredentials.getFirstName());
            preparedStatementForFirstUserRecord.setString(5, firstUserCredentials.getLastName());
            preparedStatementForFirstUserRecord.setString(6, UserType.Administrator.toString());
            if (preparedStatementForFirstUserRecord.executeUpdate() != 1) {
                throw new SQLException("First user could not be inserted!");
            }

            ResultSet rs = preparedStatementForFirstUserRecord.getGeneratedKeys();
            String userId;
            if (!rs.next()){
                throw new SQLException("User id cannot be fetched!");
            } else {
                userId = Long.toString(rs.getLong(1));
            }

            connection.commit();
            return userId;

        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException e1) {
                throw new RuntimeException(e1);
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public ApplicationUser findAccountByUsername(String username) {
        Connection connection = null;
        try {
            connection.setAutoCommit(true);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `id`, `username`, `password`, `enabled`, `first_name`, `last_name`, `user_type`, `registration_time` FROM `application_users` WHERE `username`=?");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                return  null;
            } else {
                return resultSetToApplicationUser(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ApplicationUser findAccountById(String identifier) {
        Connection connection = null;
        try {
            connection.setAutoCommit(true);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `id`, `username`, `password`, `enabled`, `first_name`, `last_name`, `user_type`, `registration_time` FROM `application_users` WHERE `id`=?");
            preparedStatement.setString(1, identifier);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                return  null;
            } else {
                return resultSetToApplicationUser(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ApplicationUser resultSetToApplicationUser(ResultSet resultSet) throws SQLException {
        ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setId(Long.toString(resultSet.getLong("id")));
        applicationUser.setUsername(resultSet.getString("username"));
        applicationUser.setPassword(resultSet.getString("password"));
        applicationUser.setEnabled(resultSet.getBoolean("enabled"));
        applicationUser.setFirstName(resultSet.getString("first_name"));
        applicationUser.setLastName(resultSet.getString("last_name"));
        applicationUser.setUserType(UserType.valueOf(resultSet.getString("user_type")));
        applicationUser.setRegistrationTime(resultSet.getTimestamp("registration_time").toInstant().getEpochSecond() / 1000);
        return applicationUser;
    }

}
