package com.bogolyandras.iotlogger.controller;

import com.bogolyandras.iotlogger.service.LogService;
import com.bogolyandras.iotlogger.value.logs.Log;
import com.bogolyandras.iotlogger.value.logs.LogAggregation;
import com.bogolyandras.iotlogger.value.logs.LogAggregationRequest;
import com.bogolyandras.iotlogger.value.logs.NewLog;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/devices")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @Secured("ROLE_USER")
    @GetMapping("/byId/{deviceId}/logs")
    public List<Log> getLogs(@PathVariable("deviceId") String deviceId) {
        return logService.getLogs(deviceId);
    }

    @Secured("ROLE_USER")
    @PostMapping("/byId/{deviceId}/logs")
    public Log addLog(
            @PathVariable("deviceId") String deviceId,
            @Valid @RequestBody NewLog newLog) {
        if (newLog.getMetric1() == null && newLog.getMetric2() == null && newLog.getMetric3() == null) {
            throw new IllegalArgumentException("You must define at least one metric for this log!");
        }
        if (newLog.getTimestamp() == null) {
            newLog.setTimestamp(Instant.now());
        }
        return logService.storeLog(deviceId, newLog);
    }

    @Secured("ROLE_USER")
    @PostMapping("/byId/{deviceId}/logs/aggregate")
    public LogAggregation addLog(
            @PathVariable("deviceId") String deviceId,
            @Valid @RequestBody LogAggregationRequest logAggregationRequest) {
        return logService.getLogs(deviceId, logAggregationRequest);
    }

}
