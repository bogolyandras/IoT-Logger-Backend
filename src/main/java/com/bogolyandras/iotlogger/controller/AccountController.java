package com.bogolyandras.iotlogger.controller;

import com.bogolyandras.iotlogger.service.AccountService;
import com.bogolyandras.iotlogger.utility.SecurityUtility;
import com.bogolyandras.iotlogger.value.account.Account;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Secured("ROLE_ADMINISTRATOR")
    @GetMapping
    public List<Account> getAccounts() {
        return accountService.getAccounts();
    }

    @Secured("ROLE_USER")
    @GetMapping("/my")
    public Account getMyAccount() {
        return accountService.getAccountById(SecurityUtility.getLoggedInUserId());
    }

    @Secured("ROLE_ADMINISTRATOR")
    @GetMapping("/byId/{userId}")
    public Account getOtherAccount(@PathVariable("userId") String userId) {
        return accountService.getAccountById(userId);
    }

    @Secured("ROLE_ADMINISTRATOR")
    @GetMapping("/byUsername/{username}")
    public Account getAccountByUsername(@PathVariable("username") String username) {
        return accountService.getAccountByUsername(username);
    }

}
