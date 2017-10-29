package com.bogolyandras.iotlogger.controller;

import com.bogolyandras.iotlogger.value.account.Account;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @GetMapping
    public Account getMyAccount() {
        throw new RuntimeException("Not implemented yet!");
    }

}
