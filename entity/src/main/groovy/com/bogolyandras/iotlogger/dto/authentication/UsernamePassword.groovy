package com.bogolyandras.iotlogger.dto.authentication

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class UsernamePassword {

    @NotNull
    @Size(min = 1, max = 60)
    String username

    @NotNull
    @Size(min = 1, max = 60)
    String password

}
