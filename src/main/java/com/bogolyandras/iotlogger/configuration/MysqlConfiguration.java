package com.bogolyandras.iotlogger.configuration;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
@Profile("mysql")
@ImportAutoConfiguration(DataSourceAutoConfiguration.class)
public class MysqlConfiguration {

    @Bean
    public Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost/test?user=minty&password=greatsqldb");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
