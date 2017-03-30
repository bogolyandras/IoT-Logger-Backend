package com.bogolyandras.iotlogger.repository.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

@Component
@Profile("sql")
public class DatabaseInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    private DataSource dataSource;

    @Autowired
    public DatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
        try {
            initializeDatabase();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeDatabase() throws SQLException {

        logger.info("Initializing database...");

        Statement statement = dataSource.getConnection().createStatement();

        statement.executeUpdate(loadSqlFile("application_users.sql"));
    }

    private String loadSqlFile(String fileName) {
        return new BufferedReader(
                new InputStreamReader(
                        getClass().getClassLoader().getResourceAsStream("sql/" + fileName)
                )
        ).lines().collect(Collectors.joining("\n"));
    }
}
