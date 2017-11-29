package com.bogolyandras.iotlogger.repository.definition;

import com.bogolyandras.iotlogger.value.device.Device;
import com.bogolyandras.iotlogger.value.device.NewDevice;

import java.util.List;

public interface DeviceRepository {

    List<Device> getDevicesForUser(String identifier);
    List<Device> getAllDevices();
    Device getDevice(String identifier);
    Device addDevice(NewDevice newDevice, String ownerId);
    void deleteDevice(String identifier);
    void deleteDeviceWithOwnerOf(String identifier, String ownerId);
    Device patchDevice(String identifier, NewDevice newDevice);
    Device patchDeviceWithOwnerOf(String identifier, NewDevice newDevice, String ownerId);

}
