package com.bogolyandras.iotlogger.configuration;

import com.bogolyandras.iotlogger.service.MysqlDataSourceConnectionEstablishment;
import com.mysql.jdbc.ReplicationDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Configuration
@Profile("mysql")
@ConfigurationProperties(prefix="mysql")
public class MysqlConfiguration {

    private final String masterHostName;
    private final Long masterPortNumber;
    private final String database;
    private final MysqlDataSourceConnectionEstablishment mysqlDataSourceConnectionEstablishment;

    //mysql.readServers[]
    private final List<String> readServers = new ArrayList<>();
    public List<String> getReadServers() {
        return readServers;
    }

    //mysql.readServerPorts[]
    private final List<Integer> readServerPorts = new ArrayList<>();
    public List<Integer> getReadServerPorts() {
        return readServerPorts;
    }

    public MysqlConfiguration(
            @Value("${mysql.master.host:#{'127.0.0.1'}}") String masterHostName,
            @Value("${mysql.master.port:#{3306}}") Long masterPortNumber,
            @Value("${mysql.database:#{'iotlogger'}}") String database,
            MysqlDataSourceConnectionEstablishment mysqlDataSourceConnectionEstablishment) {
        this.masterHostName = masterHostName;
        this.masterPortNumber = masterPortNumber;
        this.database = database;
        this.mysqlDataSourceConnectionEstablishment = mysqlDataSourceConnectionEstablishment;
    }

    @Bean
    public MysqlDataSource dataSource() throws SQLException {
        return new MysqlDataSourceImplementation(masterHostName, masterPortNumber, database, readServers, readServerPorts, mysqlDataSourceConnectionEstablishment);
    }

    public interface MysqlDataSource {

        Connection getWriteCapableConnection() throws SQLException;
        Connection getReadOnlyConnection() throws SQLException;

    }

    private static class MysqlDataSourceImplementation implements MysqlDataSource {

        private final String masterConnectionString;
        private final List<String> readOnlyConnectionStrings = new ArrayList<>();

        private final MysqlDataSourceConnectionEstablishment mysqlDataSourceConnectionEstablishment;

        private MysqlDataSourceImplementation(
                String masterHostName,
                Long masterPortNumber,
                String database,
                List<String> readServers,
                List<Integer> readServerPorts,
                MysqlDataSourceConnectionEstablishment mysqlDataSourceConnectionEstablishment) {

            this.mysqlDataSourceConnectionEstablishment = mysqlDataSourceConnectionEstablishment;

            if (readServers.size() != readServerPorts.size()) {
                throw new RuntimeException("Configuration error, you must provide server addresses and ports as well, the same amount!");
            }

            if (readServers.size() > 0) {
                for (int i = 0; i < readServers.size(); i++) {
                    readOnlyConnectionStrings.add("jdbc:mysql:replication://" + readServers.get(i) + ":" + readServerPorts.get(i) + "," + readServers.get(i) + ":" + readServerPorts.get(i) + "/" + database);
                }
            }

            masterConnectionString = "jdbc:mysql:replication://" + masterHostName + ":" + masterPortNumber + "," + masterHostName + ":" + masterPortNumber + "/" + database;
            readOnlyConnectionStrings.add(masterConnectionString);
        }

        private Connection createConnection(boolean readOnly) throws SQLException, InterruptedException, ExecutionException {

            Connection conn = null;
            if (readOnly) {

                List<CompletableFuture<Connection>> possibleConnections = new ArrayList<>();
                for (String readOnlyConnectionString : readOnlyConnectionStrings) {
                    possibleConnections.add(mysqlDataSourceConnectionEstablishment.establishConnection(readOnlyConnectionString));
                }

                Thread.sleep(50);
                for (int i = 0; i < 100; i++) {
                    for (CompletableFuture<Connection> possibleConnection : possibleConnections) {
                        Connection now = possibleConnection.getNow(null);
                        if (now != null) {
                            return now;
                        }
                    }
                    Thread.sleep(200);
                }

            } else {
                CompletableFuture<Connection> connectionCompletableFuture = mysqlDataSourceConnectionEstablishment.establishConnection(masterConnectionString);
                conn = connectionCompletableFuture.get();
            }

            if (conn == null) {
                throw new SQLException("Cannot create mysql connection!");
            }
            return conn;
        }

        @Override
        public Connection getWriteCapableConnection() throws SQLException {
            Connection conn;
            try {
                conn = createConnection(false);
            } catch (InterruptedException | ExecutionException e) {
                throw new SQLException(e);
            }
            conn.setReadOnly(false);
            return conn;
        }

        @Override
        public Connection getReadOnlyConnection() throws SQLException {
            Connection conn;
            try {
                conn = createConnection(true);
            } catch (InterruptedException | ExecutionException e) {
                throw new SQLException(e);
            }
            conn.setReadOnly(true);
            return conn;
        }
    }

}
