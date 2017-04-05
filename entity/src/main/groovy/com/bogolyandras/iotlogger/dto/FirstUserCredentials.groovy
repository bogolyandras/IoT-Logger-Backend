package com.bogolyandras.iotlogger.dto

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class FirstUserCredentials {

    @NotNull
    @Size(min = 20, max = 20)
    String serverPassword

    @NotNull
    @Size(min = 1, max = 60)
    String username

    @NotNull
    @Size(min = 1, max = 60)
    String password

    @NotNull
    @Size(min = 1, max = 60)
    String firstName

    @NotNull
    @Size(min = 1, max = 60)
    String lastName

}
