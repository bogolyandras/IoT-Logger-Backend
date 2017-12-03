package com.bogolyandras.iotlogger.repository.definition;

import com.bogolyandras.iotlogger.value.logs.Log;
import com.bogolyandras.iotlogger.value.logs.NewLog;

import java.util.List;

public interface LogRepository {

    List<Log> getLogsForDevice(String deviceId);
    Log storeLog(String deviceId, NewLog newLog);

}
