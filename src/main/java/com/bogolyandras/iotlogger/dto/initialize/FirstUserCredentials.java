package com.bogolyandras.iotlogger.dto.initialize;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class FirstUserCredentials {

    @NotNull
    @Size(min = 20, max = 20)
    private String serverPassword;

    @NotNull
    @Size(min = 1, max = 60)
    private String username;

    @NotNull
    @Size(min = 1, max = 60)
    private String password;

    @NotNull
    @Size(min = 1, max = 60)
    private String firstName;

    @NotNull
    @Size(min = 1, max = 60)
    private String lastName;

    public String getServerPassword() {
        return serverPassword;
    }

    public void setServerPassword(String serverPassword) {
        this.serverPassword = serverPassword;
    }

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
