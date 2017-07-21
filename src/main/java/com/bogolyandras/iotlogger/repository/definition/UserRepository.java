package com.bogolyandras.iotlogger.repository.definition;

import com.bogolyandras.iotlogger.domain.ApplicationUser;
import com.bogolyandras.iotlogger.domain.InitialCredentials;
import com.bogolyandras.iotlogger.dto.FirstUserCredentials;

public interface UserRepository {

    InitialCredentials getInitialCredentials(String passwordIfNotInitialized);
    String disableInitialCredentialsAndAddFirstUser(FirstUserCredentials firstUserCredentials);
    ApplicationUser findAccountByUsername(String username);
    ApplicationUser findAccountById(String identifier);

}
