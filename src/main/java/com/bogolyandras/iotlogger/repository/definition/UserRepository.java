package com.bogolyandras.iotlogger.repository.definition;

import com.bogolyandras.iotlogger.domain.user.ApplicationUser;

public interface UserRepository {

    ApplicationUser findAccountByUsername(String username);
    ApplicationUser findAccountById(String identifier);

}
