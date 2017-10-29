package com.bogolyandras.iotlogger.repository.definition;

import com.bogolyandras.iotlogger.value.account.ApplicationUser;

public interface UserRepository {

    ApplicationUser findAccountByUsername(String username);
    ApplicationUser findAccountById(String identifier);

}
