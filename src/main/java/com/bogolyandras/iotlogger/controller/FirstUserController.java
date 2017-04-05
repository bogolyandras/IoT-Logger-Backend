package com.bogolyandras.iotlogger.controller;

import com.bogolyandras.iotlogger.dto.FirstUserCredentials;
import com.bogolyandras.iotlogger.dto.FirstUserStatus;
import com.bogolyandras.iotlogger.service.FirstUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/firstUser")
public class FirstUserController {

    private FirstUserService firstUserService;

    @Autowired
    public FirstUserController(FirstUserService firstUserService) {
        this.firstUserService = firstUserService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public FirstUserStatus returnStatus() {
        return FirstUserStatus.builder()
                .initialized(firstUserService.isFirstUserSet())
                .build();
    }

    @RequestMapping(method = RequestMethod.PUT)
    public void initializeFirstUser(@Valid @RequestBody FirstUserCredentials firstUserCredentials) {
        firstUserService.initializeFirstUser(firstUserCredentials);
    }

}
