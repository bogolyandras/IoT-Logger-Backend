package com.bogolyandras.iotlogger.dto.authentication;

public class JwtToken {

    private String token;

    public JwtToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

}