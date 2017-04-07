package com.bogolyandras.iotlogger.dto


class Error {

    Error(Exception e) {
        this.message = e.getMessage()
    }
    String message

}
