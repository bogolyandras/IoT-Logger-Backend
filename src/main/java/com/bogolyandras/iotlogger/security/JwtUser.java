package com.bogolyandras.iotlogger.security;

import java.util.List;

public class JwtUser {

    private String id;
    private String username;
    private boolean enabled;
    private List<String> authorities;

    public JwtUser(String id, String username, boolean enabled, List<String> authorities) {
        this.id = id;
        this.username = username;
        this.enabled = enabled;
        this.authorities = authorities;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

}
