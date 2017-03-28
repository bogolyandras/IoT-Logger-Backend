package com.bogolyandras.iotlogger.utility;


import com.bogolyandras.iotlogger.security.JwtAuthenticationToken;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtility {

    public static String getLoggedInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken)) {
            throw new AccessDeniedException("User is not authenticated!");
        }
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken)authentication;
        return jwtAuthenticationToken.getJwtUser().getId();
    }

}
