package com.hcc.services;

import com.hcc.dtos.AuthCredentialRequest;
import com.hcc.dtos.AuthCredentialResponse;
import com.hcc.entities.Authority;
import com.hcc.entities.User;
import com.hcc.repositories.UserRepository;
import com.hcc.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class LoginService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailServiceImpl userDetailServiceImp;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;

    private final Logger log = LogManager.getLogger(LoginService.class);

    /**
     * Login endpoint. This will take the username and password from AuthCredentialRequest and authenticate it.
     * If the username and/or password is invalid, it will throw an AuthenticationException.
     * @param request the username and password
     * @return an OK from ResponseEntity if validated
     */
    public AuthCredentialResponse login(AuthCredentialRequest request) {
        String token = "";
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            //Generate JWT token
            token = jwtUtil.generateToken((UserDetails) authentication.getPrincipal());
        } catch (AuthenticationException e) {
            log.error("Login error: ", e);
        }
        return new AuthCredentialResponse(token);
    }

    /**
     * Validates the token to confirm user. If the token is valid, it will send a 200 OK Response.
     * If the token is not valid, it will send a 401 Unauthorized Response.
     * Throws a regular Exception as a plethora of exceptions can be thrown from validation.
     * @param token the token to authenticate
     * @return a 200 OK Status if validated, or else a 401 Unauthorized status
     */
    public List<String> validateToken(String token) {
        boolean isValid = false;
        try {
            String username = jwtUtil.getUsernameFromToken(token);
            UserDetails userDetails = userDetailServiceImp.loadUserByUsername(username);
            isValid = jwtUtil.validateToken(token, userDetails);

            if (!isValid) {
                log.error("Invalid token");
            } else {
                User user = userRepository.findByUsername(username).orElseThrow();
                return user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
            }

        } catch (ExpiredJwtException e) {
            log.error("Token has expired: ", e);
        } catch (UsernameNotFoundException | NoSuchElementException e) {
            log.error("No username found: ", e);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Token validation error: ", e);
        }

        return new ArrayList<>();
    }
}
