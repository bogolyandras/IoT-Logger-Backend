package com.bogolyandras.iotlogger.repository.definition;

import com.bogolyandras.iotlogger.domain.initialize.InitialCredentials;
import com.bogolyandras.iotlogger.dto.initialize.FirstUserCredentials;

public interface InitializationRepository {

    InitialCredentials getInitialCredentials(String passwordIfNotInitialized);
    String disableInitialCredentialsAndAddFirstUser(FirstUserCredentials firstUserCredentials);

}
