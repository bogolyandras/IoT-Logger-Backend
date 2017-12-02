package com.bogolyandras.iotlogger.service;

import com.bogolyandras.iotlogger.repository.definition.DeviceRepository;
import com.bogolyandras.iotlogger.utility.SecurityUtility;
import com.bogolyandras.iotlogger.value.device.Device;
import com.bogolyandras.iotlogger.value.device.NewDevice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public Device addDevice(NewDevice newDevice) {
        return deviceRepository.addDevice(newDevice, SecurityUtility.getLoggedInUserId());
    }

    public List<Device> getMyDevices() {
        return deviceRepository.getDevicesForUser(SecurityUtility.getLoggedInUserId());
    }
    
    public List<Device> getAlldevices() {
        return deviceRepository.getAllDevices();
    }
    
    public Device getDevice(String identifier) {
        Device device = deviceRepository.getDevice(identifier);
        if (!device.getOwnerId().equals(SecurityUtility.getLoggedInUserId()) && !SecurityUtility.isAdminstrator()) {
            throw new AccessDeniedException("You have no right to view device " + identifier);
        }
        return device;
    }

    public void deleteDevice(String identifier) {
        if (SecurityUtility.isAdminstrator()) {
            deviceRepository.deleteDevice(identifier);
        } else {
            deviceRepository.deleteDeviceWithOwnerOf(identifier, SecurityUtility.getLoggedInUserId());
        }
    }

    public Device patchDevice(String identifier, NewDevice newDevice) {
        if (SecurityUtility.isAdminstrator()) {
            return deviceRepository.patchDevice(identifier, newDevice);
        } else {
            return deviceRepository.patchDeviceWithOwnerOf(identifier, newDevice, SecurityUtility.getLoggedInUserId());
        }
    }

}
