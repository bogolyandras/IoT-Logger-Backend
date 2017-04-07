package com.bogolyandras.iotlogger.entity

import groovy.transform.builder.Builder

import java.time.Instant

@Builder
class ApplicationUser {

    String id

    Long registrationTime

    String username

    String password

    String firstName

    String lastName

    Boolean enabled

    UserType userType

}
