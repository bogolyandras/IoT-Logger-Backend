package com.bogolyandras.iotlogger.repository.mysql;

import com.bogolyandras.iotlogger.domain.initialize.InitialCredentials;
import com.bogolyandras.iotlogger.domain.user.UserType;
import com.bogolyandras.iotlogger.dto.initialize.FirstUserCredentials;
import com.bogolyandras.iotlogger.repository.definition.InitializationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;

public class InitializationRepositoryImplementation implements InitializationRepository {

    private static final Logger logger = LoggerFactory.getLogger(InitializationRepositoryImplementation.class);

    private final DataSource dataSource;

    public InitializationRepositoryImplementation(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public InitialCredentials getInitialCredentials(String passwordIfNotInitialized) {

        try (Connection connection = dataSource.getConnection()) {

            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.setAutoCommit(false);

            try {
                ResultSet resultSet = connection
                        .createStatement()
                        .executeQuery("SELECT `initialized`, `password` FROM `initial_credentials` WHERE `unique_row` = 1");

                if (!resultSet.next()) {

                    PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `initial_credentials`(`unique_row`, `password`, `initialized`) VALUES (?, ?, ?)");
                    preparedStatement.setBoolean(1, true);
                    preparedStatement.setString(2, passwordIfNotInitialized);
                    preparedStatement.setBoolean(3, false);
                    preparedStatement.executeUpdate();

                    connection.commit();

                    return new InitialCredentials(passwordIfNotInitialized,false);

                } else {

                    String password = resultSet.getString("password");
                    boolean initialized = resultSet.getBoolean("initialized");

                    connection.commit();

                    return new InitialCredentials(password, initialized);
                }

            } catch (SQLException e) {
                connection.rollback();
                connection.setAutoCommit(true);
                return null;
            }

        } catch (SQLException e) {
            logger.warn("Can't determine initial stuff", e);
            return null;
        }

    }

    @Override
    public String disableInitialCredentialsAndAddFirstUser(FirstUserCredentials firstUserCredentials) {

        try (Connection connection = dataSource.getConnection()) {

            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);

            try {

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

                connection.rollback();
                connection.setAutoCommit(true);
                return null;

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
