package com.bogolyandras.iotlogger.entity

import java.time.Instant


class ApplicationUser {

    String id

    Instant registrationTime

    String username

    String password

    String firstName

    String lastName

    Boolean enabled

    UserType userType

}
