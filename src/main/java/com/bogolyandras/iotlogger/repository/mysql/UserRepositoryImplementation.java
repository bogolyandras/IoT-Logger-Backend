package com.bogolyandras.iotlogger.repository.mysql;

import com.bogolyandras.iotlogger.value.account.ApplicationUser;
import com.bogolyandras.iotlogger.repository.definition.UserRepository;
import com.bogolyandras.iotlogger.value.account.NewAccount;
import com.bogolyandras.iotlogger.value.account.NewAccountWithPasswordHash;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
@Profile("mysql")
public class UserRepositoryImplementation implements UserRepository {

    private final DataSource dataSource;

    public UserRepositoryImplementation(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ApplicationUser findAccountByUsername(String username) {

        try (Connection connection = dataSource.getConnection()) {

            connection.setAutoCommit(true);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `id`, `username`, `password`, `enabled`, `first_name`, `last_name`, `user_type`, `registration_time` FROM `application_users` WHERE `username`=?");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                return null;
            } else {
                return resultSetToApplicationUser(resultSet);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public ApplicationUser findAccountById(String identifier) {

        try (Connection connection = dataSource.getConnection()) {

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

    @Override
    public List<ApplicationUser> getAllUsers() {

        try (Connection connection = dataSource.getConnection()) {

            connection.setAutoCommit(true);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `id`, `username`, `password`, `enabled`, `first_name`, `last_name`, `user_type`, `registration_time` FROM `application_users`");
            ResultSet resultSet = preparedStatement.executeQuery();
            List<ApplicationUser> applicationUsers = new ArrayList<>();
            while(resultSet.next()) {
                applicationUsers.add(resultSetToApplicationUser(resultSet));
            }
            return applicationUsers;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public ApplicationUser addAccount(NewAccountWithPasswordHash newAccountWithPasswordHash) {

        NewAccount newAccount = newAccountWithPasswordHash.getNewAccount();

        try (Connection connection = dataSource.getConnection()) {

            connection.setAutoCommit(true);

            Date registrationDate = new Date();

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO `application_users`(`username`, `password`, `enabled`, `first_name`, `last_name`, `user_type`, `registration_time`) " +
                            "VALUES (?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, newAccount.getUsername());
            preparedStatement.setString(2, newAccountWithPasswordHash.getPasswordHash());
            preparedStatement.setBoolean(3, true);
            preparedStatement.setString(4, newAccount.getFirstName());
            preparedStatement.setString(5, newAccount.getLastName());
            preparedStatement.setString(6, newAccount.getUserType().toString());
            preparedStatement.setTimestamp(7, new Timestamp(registrationDate.getTime()));
            if (preparedStatement.executeUpdate() != 1) {
                throw new SQLException("User could not be inserted!");
            }

            ResultSet rs = preparedStatement.getGeneratedKeys();
            String userId;
            if (!rs.next()){
                throw new SQLException("User id cannot be fetched!");
            } else {
                userId = Long.toString(rs.getLong(1));
            }

            ApplicationUser applicationUser = new ApplicationUser();
            applicationUser.setId(userId);
            applicationUser.setUsername(newAccount.getUsername());
            applicationUser.setPassword(newAccountWithPasswordHash.getPasswordHash());
            applicationUser.setEnabled(true);
            applicationUser.setFirstName(newAccount.getFirstName());
            applicationUser.setLastName(newAccount.getLastName());
            applicationUser.setUserType(newAccount.getUserType());
            applicationUser.setRegistrationTime(registrationDate.toInstant());

            return applicationUser;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public ApplicationUser patchAccount(String identifier, NewAccountWithPasswordHash newAccountWithPasswordHash) {

        NewAccount newAccount = newAccountWithPasswordHash.getNewAccount();

        try (Connection connection = dataSource.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE `application_users` SET " +
                            "`username` = ?, " +
                            ((newAccountWithPasswordHash.getPasswordHash() != null) ? "`password` = ?, " : "") +
                            "`enabled` = ?, " +
                            "`first_name` = ?, " +
                            "`last_name` = ?, " +
                            "`user_type` = ?, " +
                            "WHERE `id` = ?");

            preparedStatement.setString(1, newAccount.getUsername());
            int parameterIndexMinus = 0;
            if (newAccountWithPasswordHash.getPasswordHash() != null) {
                preparedStatement.setString(2, newAccountWithPasswordHash.getPasswordHash());
            } else {
                parameterIndexMinus++;
            }
            preparedStatement.setBoolean(3 - parameterIndexMinus, true);
            preparedStatement.setString(4 - parameterIndexMinus, newAccount.getFirstName());
            preparedStatement.setString(5 - parameterIndexMinus, newAccount.getLastName());
            preparedStatement.setString(6 - parameterIndexMinus, newAccount.getUserType().toString());
            preparedStatement.setString(7 - parameterIndexMinus, identifier);

            if (preparedStatement.executeUpdate() != 1) {
                throw new SQLException("User " + identifier + " could not be updated!");
            }

            return findAccountById(identifier);

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
        applicationUser.setUserType(ApplicationUser.UserType.valueOf(resultSet.getString("user_type")));
        applicationUser.setRegistrationTime(resultSet.getTimestamp("registration_time").toInstant());
        return applicationUser;
    }

}
