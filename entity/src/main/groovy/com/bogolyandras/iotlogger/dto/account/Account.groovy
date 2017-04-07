package com.bogolyandras.iotlogger.dto.account

import groovy.transform.builder.Builder

@Builder
class Account {

    String username
    String firstName
    String lastName
    Long registrationTime

}
