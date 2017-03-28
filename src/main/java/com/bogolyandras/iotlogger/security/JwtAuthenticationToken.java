package com.bogolyandras.iotlogger.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.stream.Collectors;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private JwtUser jwtUser;

    public JwtAuthenticationToken(JwtUser jwtUser) {
        super(jwtUser.getAuthorities().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        this.jwtUser = jwtUser;
        if (jwtUser.isEnabled()) {
            super.setAuthenticated(true);
        }
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return jwtUser;
    }

    public JwtUser getJwtUser() {
        return jwtUser;
    }

}
