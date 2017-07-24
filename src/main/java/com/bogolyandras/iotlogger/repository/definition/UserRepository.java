package com.bogolyandras.iotlogger.repository.definition;

import com.bogolyandras.iotlogger.domain.user.ApplicationUser;
import com.bogolyandras.iotlogger.domain.initialize.InitialCredentials;
import com.bogolyandras.iotlogger.dto.initialize.FirstUserCredentials;

public interface UserRepository {

    InitialCredentials getInitialCredentials(String passwordIfNotInitialized);
    String disableInitialCredentialsAndAddFirstUser(FirstUserCredentials firstUserCredentials);
    ApplicationUser findAccountByUsername(String username);
    ApplicationUser findAccountById(String identifier);

}
