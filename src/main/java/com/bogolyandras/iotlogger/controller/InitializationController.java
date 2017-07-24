package com.bogolyandras.iotlogger.controller;

import com.bogolyandras.iotlogger.dto.initialize.FirstUserCredentials;
import com.bogolyandras.iotlogger.dto.initialize.FirstUserStatus;
import com.bogolyandras.iotlogger.dto.authentication.JwtToken;
import com.bogolyandras.iotlogger.service.InitializationService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/account/firstAccount")
public class InitializationController {

    private final InitializationService initializationService;

    public InitializationController(InitializationService initializationService) {
        this.initializationService = initializationService;
    }

    @GetMapping
    public FirstUserStatus returnStatus() {
        return new FirstUserStatus(initializationService.isFirstUserSet());
    }

    @PostMapping
    public JwtToken initializeFirstUser(@Valid @RequestBody FirstUserCredentials firstUserCredentials) {
        return initializationService.initializeFirstUser(firstUserCredentials);
    }

}
