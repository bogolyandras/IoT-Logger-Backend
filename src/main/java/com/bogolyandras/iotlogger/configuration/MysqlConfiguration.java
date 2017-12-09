package com.bogolyandras.iotlogger.configuration;

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

@Configuration
@Profile("mysql")
@ConfigurationProperties(prefix="mysql")
public class MysqlConfiguration {

    private final String masterHostName;
    private final Long masterPortNumber;
    private final String database;

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
            @Value("${mysql.database:#{'iotlogger'}}") String database) {
        this.masterHostName = masterHostName;
        this.masterPortNumber = masterPortNumber;
        this.database = database;
    }

    @Bean
    public MysqlDataSource dataSource() throws SQLException {
        return new MysqlDataSourceImplementation(masterHostName, masterPortNumber, database, readServers, readServerPorts);
    }

    public interface MysqlDataSource {

        Connection getWriteCapableConnection() throws SQLException;
        Connection getReadOnlyConnection() throws SQLException;

    }

    private static class MysqlDataSourceImplementation implements MysqlDataSource {

        private final Properties props = new Properties();
        private final String connectionString;
        private final String readOnlyConnectionString;
        private final ReplicationDriver driver = new ReplicationDriver();

        private MysqlDataSourceImplementation(
                String masterHostName,
                Long masterPortNumber,
                String database,
                List<String> readServers,
                List<Integer> readServerPorts)
                throws SQLException {

            props.put("autoReconnect", "true");
            props.put("roundRobinLoadBalance", "true");
            props.put("user", "root");
            props.put("useSSL", "false"); // Avoid warning messages
            props.put("allowMasterDownConnections", "true");
            props.put("allowSlavesDownConnections", "true");
            props.put("readFromMasterWhenNoSlaves", "true");

            if (readServers.size() != readServerPorts.size()) {
                throw new RuntimeException("Configuration error, you must provide server addresses and ports as well, the same amount!");
            }


            if (readServers.size() > 0) {
                StringBuilder replicaString = new StringBuilder();
                for (int i = 0; i < readServers.size(); i++) {
                    replicaString.append(",").append(readServers.get(i)).append(":").append(readServerPorts.get(i));
                }
                connectionString = "jdbc:mysql:replication://" + masterHostName + ":" + masterPortNumber + replicaString + "/" + database;
                readOnlyConnectionString = "jdbc:mysql:replication://" + readServers.get(0) + ":" + readServerPorts.get(0) + replicaString + "/" + database;
            } else {
                connectionString = "jdbc:mysql:replication://" + masterHostName + ":" + masterPortNumber + "," + masterHostName + ":" + masterPortNumber + "/" + database;
                readOnlyConnectionString = connectionString;
            }

        }

        private Connection createConnection(boolean readOnly) throws SQLException {
            Connection conn;
            if (readOnly) {
                conn = driver.connect(readOnlyConnectionString, props);
            } else {
                conn = driver.connect(connectionString, props);
            }

            if (conn == null) {
                throw new SQLException("Cannot create mysql connection!");
            }
            return conn;
        }

        @Override
        public Connection getWriteCapableConnection() throws SQLException {
            Connection conn = createConnection(false);
            conn.setReadOnly(false);
            return conn;
        }

        @Override
        public Connection getReadOnlyConnection() throws SQLException {
            Connection conn = createConnection(true);
            conn.setReadOnly(true);
            return conn;
        }
    }

}
