package com.bogolyandras.iotlogger.repository.definition;

import com.bogolyandras.iotlogger.dto.FirstUserCredentials;
import com.bogolyandras.iotlogger.entity.InitialCredentials;

public interface UserRepository {

    InitialCredentials getInitialCredentials();
    void addInitialCredentials(InitialCredentials initialCredentials);
    void disableInitialCredentialsAndAddFirstUser(FirstUserCredentials firstUserCredentials);

}
