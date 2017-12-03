package com.bogolyandras.iotlogger.service;

import com.bogolyandras.iotlogger.repository.definition.DeviceRepository;
import com.bogolyandras.iotlogger.repository.definition.LogRepository;
import com.bogolyandras.iotlogger.utility.SecurityUtility;
import com.bogolyandras.iotlogger.value.device.Device;
import com.bogolyandras.iotlogger.value.logs.Log;
import com.bogolyandras.iotlogger.value.logs.NewLog;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogService {

    private final DeviceRepository deviceRepository;
    private final LogRepository logRepository;

    public LogService(DeviceRepository deviceRepository, LogRepository logRepository) {
        this.deviceRepository = deviceRepository;
        this.logRepository = logRepository;
    }

    public List<Log> getLogs(String deviceId) {
        Device device = deviceRepository.getDevice(deviceId);
        if (!device.getOwnerId().equals(SecurityUtility.getLoggedInUserId())) {
            throw new AccessDeniedException("You have no right to add data to this device!");
        }
        return logRepository.getLogsForDevice(deviceId);
    }

    public Log storeLog(String deviceId, NewLog newLog) {
        Device device = deviceRepository.getDevice(deviceId);
        if (!device.getOwnerId().equals(SecurityUtility.getLoggedInUserId())) {
            throw new AccessDeniedException("You have no right to add data to this device!");
        }
        return logRepository.storeLog(deviceId, newLog);
    }

}
