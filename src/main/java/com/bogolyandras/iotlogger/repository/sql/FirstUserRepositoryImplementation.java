package com.bogolyandras.iotlogger.repository.sql;

import com.bogolyandras.iotlogger.entity.InitialCredentials;
import com.bogolyandras.iotlogger.repository.definition.FirstUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
@Profile("default")
public class FirstUserRepositoryImplementation implements FirstUserRepository {

    private DataSource dataSource;

    @Autowired
    public FirstUserRepositoryImplementation(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public InitialCredentials getInitialCredentials() {
        try {
            ResultSet resultSet = dataSource.getConnection()
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
        try {
            PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement("INSERT INTO `initial_credentials`(`unique_row`, `password`, `initialized`) VALUES (?, ?, ?)");
            preparedStatement.setBoolean(1, true);
            preparedStatement.setString(2, initialCredentials.getPassword());
            preparedStatement.setBoolean(3, initialCredentials.getInitialized());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateInitialCredentials(InitialCredentials initialCredentials) {
        try {
            PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement("UPDATE `initial_credentials` SET `password`=?,`initialized`=? WHERE `unique_row`=1");
            preparedStatement.setString(1, initialCredentials.getPassword());
            preparedStatement.setBoolean(2, initialCredentials.getInitialized());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
