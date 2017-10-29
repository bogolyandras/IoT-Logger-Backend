package com.bogolyandras.iotlogger.repository.mysql;

import com.bogolyandras.iotlogger.value.initialize.FirstUserCredentialsWithEncodedPassword;
import com.bogolyandras.iotlogger.value.initialize.InitialCredentials;
import com.bogolyandras.iotlogger.value.account.ApplicationUser;
import com.bogolyandras.iotlogger.value.initialize.FirstUserCredentials;
import com.bogolyandras.iotlogger.repository.definition.InitializationRepository;
import com.bogolyandras.iotlogger.utility.FileUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Arrays;

@Repository
@Profile("mysql")
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

                DatabaseMetaData databaseMetaData = connection.getMetaData();
                ResultSet tables = databaseMetaData.getTables(null, null, "application_properties", null);

                //Check if database have not been initialized
                if (!tables.next()) {

                    // Initialize database structure
                    for (String fileName : Arrays.asList("application_users", "application_properties")) {
                        String sqlScript = FileUtility.getResourceAsString("mysql/" + fileName + ".sql");
                        for (String scriptPart : sqlScript.split(";")) {
                            Statement statement = connection.createStatement();
                            logger.info(scriptPart);
                            statement.executeUpdate(scriptPart);
                        }
                    }

                    // Insert initial password
                    PreparedStatement statement = connection.prepareStatement("INSERT INTO `application_properties`(`property_key`, `value`) VALUES ('initial_password', ?)");
                    statement.setString(1, passwordIfNotInitialized);
                    statement.executeUpdate();

                    //Finalize the state
                    connection.commit();
                    return new InitialCredentials(passwordIfNotInitialized,false);
                }

                ResultSet resultSet = connection
                        .createStatement()
                        .executeQuery("SELECT `value` FROM `application_properties` WHERE `property_key` = 'initial_password'");

                if (!resultSet.next()) {
                    return new InitialCredentials(null, true);
                } else {
                    String password = resultSet.getString("value");
                    return new InitialCredentials(password, password == null);
                }

            } catch (SQLException e) {
                connection.rollback();
                connection.setAutoCommit(true);
                logger.warn("SQL Exception occured", e);
                return null;
            }

        } catch (SQLException e) {
            logger.warn("Can't determine initial stuff", e);
            return null;
        }

    }

    @Override
    public String disableInitialCredentialsAndAddFirstUser(FirstUserCredentialsWithEncodedPassword firstUserCredentialsWithEncodedPassword) {

        FirstUserCredentials firstUserCredentials = firstUserCredentialsWithEncodedPassword.getFirstUserCredentials();

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
                preparedStatementForFirstUserRecord.setString(2, firstUserCredentialsWithEncodedPassword.getPasswordHash());
                preparedStatementForFirstUserRecord.setBoolean(3, true);
                preparedStatementForFirstUserRecord.setString(4, firstUserCredentials.getFirstName());
                preparedStatementForFirstUserRecord.setString(5, firstUserCredentials.getLastName());
                preparedStatementForFirstUserRecord.setString(6, ApplicationUser.UserType.Administrator.toString());
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
