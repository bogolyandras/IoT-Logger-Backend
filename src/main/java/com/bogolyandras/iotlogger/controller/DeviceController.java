package com.bogolyandras.iotlogger.controller;

import com.bogolyandras.iotlogger.service.DeviceService;
import com.bogolyandras.iotlogger.value.device.Device;
import com.bogolyandras.iotlogger.value.device.NewDevice;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/devices")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Secured("ROLE_USER")
    @GetMapping
    public List<Device> getDevices() {
        throw new RuntimeException("Not implemented yet!");
    }

    @Secured("ROLE_USER")
    @GetMapping("/all")
    public List<Device> getAllDevices() {
        throw new RuntimeException("Not implemented yet!");
    }

    @Secured("ROLE_USER")
    @GetMapping("/byId/{deviceId}")
    public Device getDevice(@PathVariable("deviceId") String deviceId) {
        throw new RuntimeException("Not implemented yet!");
    }

    @Secured("ROLE_USER")
    @PostMapping
    public Device addDevice(@Valid @RequestBody NewDevice newDevice) {
        throw new RuntimeException("Not implemented yet!");
    }

    @Secured("ROLE_USER")
    @PatchMapping("/byId/{deviceId}")
    public Device patchDevice(
            @PathVariable("deviceId") String deviceId,
            @Valid @RequestBody NewDevice newDevice) {
        throw new RuntimeException("Not implemented yet!");
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/byId/{deviceId}")
    public Device patchDevice(
            @PathVariable("deviceId") String deviceId) {
        throw new RuntimeException("Not implemented yet!");
    }

}
