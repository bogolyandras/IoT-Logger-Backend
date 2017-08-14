package com.bogolyandras.iotlogger.repository.mysql;

import com.bogolyandras.iotlogger.domain.user.ApplicationUser;
import com.bogolyandras.iotlogger.repository.definition.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    private ApplicationUser resultSetToApplicationUser(ResultSet resultSet) throws SQLException {
        ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setId(Long.toString(resultSet.getLong("id")));
        applicationUser.setUsername(resultSet.getString("username"));
        applicationUser.setPassword(resultSet.getString("password"));
        applicationUser.setEnabled(resultSet.getBoolean("enabled"));
        applicationUser.setFirstName(resultSet.getString("first_name"));
        applicationUser.setLastName(resultSet.getString("last_name"));
        applicationUser.setUserType(ApplicationUser.UserType.valueOf(resultSet.getString("user_type")));
        applicationUser.setRegistrationTime(resultSet.getTimestamp("registration_time").toInstant().getEpochSecond() / 1000);
        return applicationUser;
    }

}
