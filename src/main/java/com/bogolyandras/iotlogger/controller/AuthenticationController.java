package com.bogolyandras.iotlogger.controller;

import com.bogolyandras.iotlogger.dto.authentication.JwtToken;
import com.bogolyandras.iotlogger.dto.authentication.UsernamePassword;
import com.bogolyandras.iotlogger.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("/authentication")
@RestController
public class AuthenticationController {

    private AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public JwtToken attemptLogin(@Valid @RequestBody UsernamePassword usernamePassword) {
        return authenticationService.attemptLogin(usernamePassword);
    }

}
