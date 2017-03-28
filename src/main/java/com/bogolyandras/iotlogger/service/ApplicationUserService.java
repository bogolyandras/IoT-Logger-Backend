package com.bogolyandras.iotlogger.service;

import com.bogolyandras.iotlogger.entity.ApplicationUser;
import com.bogolyandras.iotlogger.security.JwtUser;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class ApplicationUserService {

    public JwtUser loadUserByUsername(String username) throws UsernameNotFoundException {
        ApplicationUser applicationUser = new ApplicationUser(); //applicationUserRepository.findByEmail(username);
        if (applicationUser == null) {
            throw new UsernameNotFoundException("No user " + username + " found.");
        }
        return getUserDetailsFromEntity(applicationUser);
    }
    public JwtUser loadUserById(String id) throws UsernameNotFoundException {
        ApplicationUser applicationUser = new ApplicationUser(); //applicationUserRepository.findOne(Integer.parseInt(id));
        applicationUser.setEnabled(true);
        if (applicationUser == null) {
            throw new UsernameNotFoundException("No user with id of " + id + " found.");
        }
        if (!applicationUser.getEnabled()) {
            throw new DisabledException("Account has been disabled!");
        }
        return getUserDetailsFromEntity(applicationUser);
    }

    private JwtUser getUserDetailsFromEntity(ApplicationUser applicationUser) {
        List<String> authorities;
        switch (applicationUser.getUserType()) {
            case User:
                authorities = Collections.singletonList("ROLE_USER");
                break;
            case Administrator:
                authorities = Arrays.asList("ROLE_USER", "ROLE_ADMINISTRATOR");
                break;
            default:
                throw new RuntimeException("User type is not supported!");
        }
        return new JwtUser(
                applicationUser.getId(),
                applicationUser.getUsername(),
                applicationUser.getEnabled(),
                authorities
        );
    }

}
