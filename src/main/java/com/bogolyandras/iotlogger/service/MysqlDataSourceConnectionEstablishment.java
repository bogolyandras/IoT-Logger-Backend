package com.bogolyandras.iotlogger.service;

import com.mysql.jdbc.ReplicationDriver;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

@Service
public class MysqlDataSourceConnectionEstablishment {

    private final Properties props = new Properties();
    private final ReplicationDriver driver = new ReplicationDriver();

    public MysqlDataSourceConnectionEstablishment() throws SQLException {

        props.put("autoReconnect", "true");
        props.put("roundRobinLoadBalance", "true");
        props.put("user", "root");
        props.put("useSSL", "false"); // Avoid warning messages
        props.put("allowMasterDownConnections", "true");
        props.put("allowSlavesDownConnections", "true");
        props.put("readFromMasterWhenNoSlaves", "true");

    }

    @Async
    public CompletableFuture<Connection> establishConnection(String connectionsString) {
        try {
            Connection conn = driver.connect(connectionsString, props);
            return CompletableFuture.completedFuture(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
