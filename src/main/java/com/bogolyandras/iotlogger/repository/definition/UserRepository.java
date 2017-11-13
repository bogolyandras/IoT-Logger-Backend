package com.bogolyandras.iotlogger.repository.definition;

import com.bogolyandras.iotlogger.value.account.ApplicationUser;

import java.util.List;

public interface UserRepository {

    ApplicationUser findAccountByUsername(String username);
    ApplicationUser findAccountById(String identifier);
    List<ApplicationUser> getAllUsers();

}
