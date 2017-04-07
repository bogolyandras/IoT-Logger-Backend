package com.bogolyandras.iotlogger.controller;

import com.bogolyandras.iotlogger.dto.FirstUserCredentials;
import com.bogolyandras.iotlogger.dto.FirstUserStatus;
import com.bogolyandras.iotlogger.dto.authentication.JwtToken;
import com.bogolyandras.iotlogger.service.FirstAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/account/firstAccount")
public class FirstUserController {

    private FirstAccountService firstAccountService;

    @Autowired
    public FirstUserController(FirstAccountService firstAccountService) {
        this.firstAccountService = firstAccountService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public FirstUserStatus returnStatus() {
        return FirstUserStatus.builder()
                .initialized(firstAccountService.isFirstUserSet())
                .build();
    }

    @RequestMapping(method = RequestMethod.PUT)
    public JwtToken initializeFirstUser(@Valid @RequestBody FirstUserCredentials firstUserCredentials) {
        return firstAccountService.initializeFirstUser(firstUserCredentials);
    }

}
