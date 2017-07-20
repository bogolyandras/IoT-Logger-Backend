package com.bogolyandras.iotlogger.controller;

import com.bogolyandras.iotlogger.dto.FirstUserCredentials;
import com.bogolyandras.iotlogger.dto.FirstUserStatus;
import com.bogolyandras.iotlogger.dto.authentication.JwtToken;
import com.bogolyandras.iotlogger.service.FirstAccountService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/account/firstAccount")
public class FirstAccountController {

    private final FirstAccountService firstAccountService;

    public FirstAccountController(FirstAccountService firstAccountService) {
        this.firstAccountService = firstAccountService;
    }

    @GetMapping
    public FirstUserStatus returnStatus() {
        return new FirstUserStatus(firstAccountService.isFirstUserSet());
    }

    @PostMapping
    public JwtToken initializeFirstUser(@Valid @RequestBody FirstUserCredentials firstUserCredentials) {
        return firstAccountService.initializeFirstUser(firstUserCredentials);
    }

}
