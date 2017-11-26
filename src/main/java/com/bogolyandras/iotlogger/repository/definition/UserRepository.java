package com.bogolyandras.iotlogger.repository.definition;

import com.bogolyandras.iotlogger.value.account.ApplicationUser;
import com.bogolyandras.iotlogger.value.account.NewAccountWithPasswordHash;

import java.util.List;

public interface UserRepository {

    List<ApplicationUser> getAllUsers();
    ApplicationUser addAccount(NewAccountWithPasswordHash newAccount);
    ApplicationUser patchAccount(String identifier, NewAccountWithPasswordHash newAccount);
    void deleteAccount(String identifier);
    ApplicationUser findAccountByUsername(String username);
    ApplicationUser findAccountById(String identifier);

}
