package com.bogolyandras.iotlogger.entity

import groovy.transform.builder.Builder

@Builder
class InitialCredentials {

    String password
    Boolean initialized

}
