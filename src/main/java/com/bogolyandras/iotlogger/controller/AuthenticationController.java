package com.bogolyandras.iotlogger.controller;

import com.bogolyandras.iotlogger.dto.authentication.JwtToken;
import com.bogolyandras.iotlogger.dto.authentication.UsernamePassword;
import com.bogolyandras.iotlogger.service.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("/authentication")
@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping
    public JwtToken attemptLogin(@Valid @RequestBody UsernamePassword usernamePassword) {
        return authenticationService.attemptLogin(usernamePassword);
    }

}
