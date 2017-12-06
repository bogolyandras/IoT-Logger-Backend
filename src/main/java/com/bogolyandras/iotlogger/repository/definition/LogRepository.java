package com.bogolyandras.iotlogger.repository.definition;

import com.bogolyandras.iotlogger.value.logs.Log;
import com.bogolyandras.iotlogger.value.logs.LogAggregation;
import com.bogolyandras.iotlogger.value.logs.LogAggregationRequest;
import com.bogolyandras.iotlogger.value.logs.NewLog;

import java.util.List;

public interface LogRepository {

    List<Log> getLogsForDevice(String deviceId);
    LogAggregation getLogAggregation(String deviceId, LogAggregationRequest logAggregationRequest);
    Log storeLog(String deviceId, NewLog newLog);

}
