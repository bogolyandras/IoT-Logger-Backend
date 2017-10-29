package com.bogolyandras.iotlogger.controller;

import com.bogolyandras.iotlogger.value.initialize.FirstUserCredentials;
import com.bogolyandras.iotlogger.value.initialize.FirstUserStatus;
import com.bogolyandras.iotlogger.value.authentication.JwtToken;
import com.bogolyandras.iotlogger.service.InitializationService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/accounts/firstAccount")
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
