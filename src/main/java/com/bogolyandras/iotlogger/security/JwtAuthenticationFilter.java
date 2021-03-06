package com.bogolyandras.iotlogger.security;


import com.bogolyandras.iotlogger.exception.JwtException;
import com.bogolyandras.iotlogger.service.AuthenticationService;
import com.bogolyandras.iotlogger.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


public class JwtAuthenticationFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public JwtAuthenticationFilter(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void destroy() { }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String authorizationHeader = httpRequest.getHeader("Authorization");
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String jwtToken = authorizationHeader.replace("Bearer ", "");
                String userId;
                try {
                    userId = jwtService.verifyToken(jwtToken);
                    JwtUser jwtUser = authenticationService.loadUserById(userId);
                    Authentication authentication = new JwtAuthenticationToken(jwtUser);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (JwtException | UsernameNotFoundException e) {
                    logger.info(e.toString());
                }
            }
        }
        chain.doFilter(request, response);
    }

}
