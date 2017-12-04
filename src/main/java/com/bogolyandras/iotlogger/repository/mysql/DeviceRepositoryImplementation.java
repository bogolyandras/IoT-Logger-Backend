package com.bogolyandras.iotlogger.repository.mysql;

import com.bogolyandras.iotlogger.configuration.MysqlConfiguration;
import com.bogolyandras.iotlogger.repository.definition.DeviceRepository;
import com.bogolyandras.iotlogger.value.device.Device;
import com.bogolyandras.iotlogger.value.device.NewDevice;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@Profile("mysql")
public class DeviceRepositoryImplementation implements DeviceRepository {

    private final MysqlConfiguration.MysqlDataSource dataSource;

    public DeviceRepositoryImplementation(MysqlConfiguration.MysqlDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Device> getDevicesForUser(String identifier) {

        try (Connection connection = dataSource.getReadOnlyConnection()) {

            connection.setAutoCommit(true);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `id`, `owner_id`, `name`, `description` FROM `devices` WHERE `owner_id`=?");
            preparedStatement.setString(1, identifier);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Device> devices = new ArrayList<>();
            while(resultSet.next()) {
                devices.add(resultSetToDevice(resultSet));
            }
            return devices;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Device> getAllDevices() {

        try (Connection connection = dataSource.getReadOnlyConnection()) {

            connection.setAutoCommit(true);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `id`, `owner_id`, `name`, `description` FROM `devices`");
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Device> devices = new ArrayList<>();
            while(resultSet.next()) {
                devices.add(resultSetToDevice(resultSet));
            }
            return devices;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Device getDevice(String identifier) {

        try (Connection connection = dataSource.getReadOnlyConnection()) {

            connection.setAutoCommit(true);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `id`, `owner_id`, `name`, `description` FROM `devices` WHERE `id`=?");
            preparedStatement.setString(1, identifier);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                return  null;
            } else {
                return resultSetToDevice(resultSet);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Device addDevice(NewDevice newDevice, String ownerId) {

        try (Connection connection = dataSource.getWriteCapableConnection()) {

            connection.setAutoCommit(true);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO `devices`(`owner_id`, `name`, `description`) " +
                            "VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, ownerId);
            preparedStatement.setString(2, newDevice.getName());
            preparedStatement.setString(3, newDevice.getDescription());
            if (preparedStatement.executeUpdate() != 1) {
                throw new SQLException("Device could not be inserted!");
            }

            ResultSet rs = preparedStatement.getGeneratedKeys();
            String deviceId;
            if (!rs.next()){
                throw new SQLException("Device id cannot be fetched!");
            } else {
                deviceId = Long.toString(rs.getLong(1));
            }

            return new Device(
                    deviceId,
                    ownerId,
                    newDevice.getName(),
                    newDevice.getDescription()
            );

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void deleteDevice(String identifier) {

        try (Connection connection = dataSource.getWriteCapableConnection()) {

            connection.setAutoCommit(true);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM `devices` " +
                            "WHERE `id` = ?");
            preparedStatement.setString(1, identifier);

            if (preparedStatement.executeUpdate() != 1) {
                throw new SQLException("Device " + identifier + " could not be deleted!");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void deleteDeviceWithOwnerOf(String identifier, String ownerId) {

        try (Connection connection = dataSource.getWriteCapableConnection()) {

            connection.setAutoCommit(true);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM `devices` " +
                            "WHERE `id` = ? AND `owner_id` = ?");
            preparedStatement.setString(1, identifier);
            preparedStatement.setString(2, ownerId);

            if (preparedStatement.executeUpdate() != 1) {
                throw new SQLException("Device " + identifier + " could not be deleted!");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Device patchDevice(String identifier, NewDevice newDevice) {

        try (Connection connection = dataSource.getWriteCapableConnection()) {

            connection.setAutoCommit(true);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE `devices` SET " +
                            "`name` = ?, " +
                            "`description` = ? " +
                            "WHERE `id` = ?");

            preparedStatement.setString(1, newDevice.getName());
            preparedStatement.setString(2, newDevice.getDescription());
            preparedStatement.setString(3, identifier);

            if (preparedStatement.executeUpdate() != 1) {
                throw new SQLException("Device " + identifier + " could not be updated!");
            }

            return getDevice(identifier);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Device patchDeviceWithOwnerOf(String identifier, NewDevice newDevice, String ownerId) {

        try (Connection connection = dataSource.getWriteCapableConnection()) {

            connection.setAutoCommit(true);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE `devices` SET " +
                            "`name` = ?, " +
                            "`description` = ? " +
                            "WHERE `id` = ? " +
                            "AND `owner_id` = ?");

            preparedStatement.setString(1, newDevice.getName());
            preparedStatement.setString(2, newDevice.getDescription());
            preparedStatement.setString(3, identifier);
            preparedStatement.setString(4, ownerId);

            if (preparedStatement.executeUpdate() != 1) {
                throw new SQLException("Device " + identifier + " could not be updated!");
            }

            return getDevice(identifier);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private Device resultSetToDevice(ResultSet resultSet) throws SQLException {
        return new Device(
                Long.toString(resultSet.getLong("id")),
                Long.toString(resultSet.getLong("owner_id")),
                resultSet.getString("name"),
                resultSet.getString("description")
        );
    }

}
