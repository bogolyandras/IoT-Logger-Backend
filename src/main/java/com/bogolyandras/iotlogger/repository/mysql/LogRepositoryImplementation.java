package com.bogolyandras.iotlogger.repository.mysql;

import com.bogolyandras.iotlogger.configuration.MysqlConfiguration;
import com.bogolyandras.iotlogger.repository.definition.LogRepository;
import com.bogolyandras.iotlogger.value.logs.Log;
import com.bogolyandras.iotlogger.value.logs.LogAggregation;
import com.bogolyandras.iotlogger.value.logs.LogAggregationRequest;
import com.bogolyandras.iotlogger.value.logs.NewLog;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
@Profile("mysql")
public class LogRepositoryImplementation implements LogRepository {

    private final MysqlConfiguration.MysqlDataSource dataSource;

    public LogRepositoryImplementation(MysqlConfiguration.MysqlDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Log> getLogsForDevice(String deviceId) {

        try (Connection connection = dataSource.getReadOnlyConnection()) {

            connection.setAutoCommit(true);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `id`, `device_id`, `data_time`, `metric_1`, `metric_2`, `metric_3` FROM `device_logs` WHERE `device_id`=?");
            preparedStatement.setString(1, deviceId);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Log> logs = new ArrayList<>();
            while(resultSet.next()) {
                logs.add(resultSetToLog(resultSet));
            }
            return logs;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public LogAggregation getLogAggregation(String deviceId, LogAggregationRequest logAggregationRequest) {

        return new LogAggregation(null);

    }

    @Override
    public Log storeLog(String deviceId, NewLog newLog) {

        try (Connection connection = dataSource.getWriteCapableConnection()) {

            connection.setAutoCommit(true);

            Date registrationDate = new Date();

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO `device_logs`(`device_id`, `data_time`, `metric_1`, `metric_2`, `metric_3`) " +
                            "VALUES (?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, Long.parseLong(deviceId));
            preparedStatement.setTimestamp(2, Timestamp.from(newLog.getTimestamp()));
            preparedStatement.setBigDecimal(3, newLog.getMetric1());
            preparedStatement.setBigDecimal(4, newLog.getMetric2());
            preparedStatement.setBigDecimal(5, newLog.getMetric3());
            if (preparedStatement.executeUpdate() != 1) {
                throw new SQLException("Log could not be inserted!");
            }

            ResultSet rs = preparedStatement.getGeneratedKeys();
            String logId;
            if (!rs.next()){
                throw new SQLException("Log id cannot be fetched!");
            } else {
                logId = Long.toString(rs.getLong(1));
            }

            return new Log(
                    logId,
                    newLog.getTimestamp(),
                    newLog.getMetric1(),
                    newLog.getMetric2(),
                    newLog.getMetric3()
            );

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private Log resultSetToLog(ResultSet resultSet) throws SQLException {
        return new Log(
                Long.toString(resultSet.getLong("id")),
                resultSet.getTimestamp("data_time").toInstant(),
                resultSet.getBigDecimal("metric_1"),
                resultSet.getBigDecimal("metric_2"),
                resultSet.getBigDecimal("metric_3")
        );
    }
}