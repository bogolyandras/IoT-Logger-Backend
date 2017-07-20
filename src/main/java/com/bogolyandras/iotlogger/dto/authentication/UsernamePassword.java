package com.bogolyandras.iotlogger.dto.authentication;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UsernamePassword {

    @NotNull
    @Size(min = 1, max = 60)
    private String username;

    @NotNull
    @Size(min = 1, max = 60)
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
