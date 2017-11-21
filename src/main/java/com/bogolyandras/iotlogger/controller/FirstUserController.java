package com.bogolyandras.iotlogger.controller;

import com.bogolyandras.iotlogger.value.initialize.FirstUserCredentials;
import com.bogolyandras.iotlogger.value.initialize.FirstUserStatus;
import com.bogolyandras.iotlogger.value.authentication.JwtToken;
import com.bogolyandras.iotlogger.service.FirstUserService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/accounts/firstAccount")
public class FirstUserController {

    private final FirstUserService firstUserService;

    public FirstUserController(FirstUserService firstUserService) {
        this.firstUserService = firstUserService;
    }

    @GetMapping
    public FirstUserStatus returnStatus() {
        return new FirstUserStatus(firstUserService.isFirstUserSet());
    }

    @PostMapping
    public JwtToken initializeFirstUser(@Valid @RequestBody FirstUserCredentials firstUserCredentials) {
        return firstUserService.initializeFirstUser(firstUserCredentials);
    }

}
