package com.bogolyandras.iotlogger.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("mysql")
public class MysqlConfiguration {

    private final String hostName;
    private final Long portNumber;
    private final String userName;
    private final String password;
    private final String database;

    public MysqlConfiguration(
            @Value("${mysql.host:#{'localhost'}}") String hostName,
            @Value("${mysql.port:#{3306}}") Long portNumber,
            @Value("${mysql.username:#{'root'}}") String userName,
            @Value("${mysql.password:#{null}}") String password,
            @Value("${mysql.database:#{'iotlogger'}}") String database) {
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.userName = userName;
        this.password = password;
        this.database = database;
    }

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://" + hostName + ":" + portNumber + "/" + database + "?verifyServerCertificate=false");
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        return dataSource;
    }

}
