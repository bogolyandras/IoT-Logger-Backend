package com.bogolyandras.iotlogger.controller;

import com.bogolyandras.iotlogger.dto.account.Account;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountController {

    @RequestMapping(method = RequestMethod.GET)
    public Account getMyAccount() {
        throw new RuntimeException("Not implemented yet!");
    }

}
