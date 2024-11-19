package com.hcc.services;

import com.hcc.dtos.AuthCredentialRequest;
import com.hcc.dtos.AuthCredentialResponse;
import com.hcc.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailServiceImpl userDetailServiceImp;
    @Autowired
    private JwtUtil jwtUtil;

    private final Logger log = LogManager.getLogger(LoginService.class);

    /**
     * Login endpoint. This will take the username and password from AuthCredentialRequest and authenticate it.
     * If the username and/or password is invalid, it will throw an AuthenticationException.
     * @param request the username and password
     * @return an OK from ResponseEntity if validated
     */
    public ResponseEntity<?> login(AuthCredentialRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            //Generate JWT token
            String token = jwtUtil.generateToken((UserDetails) authentication.getPrincipal());

            return ResponseEntity.ok(new AuthCredentialResponse(token));
        } catch (AuthenticationException e) {
            log.error("Login error: ", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    /**
     * Validates the token to confirm user. If the token is valid, it will send a 200 OK Response.
     * If the token is not valid, it will send a 401 Unauthorized Response.
     * Throws a regular Exception as a plethora of exceptions can be thrown from validation.
     * @param token the token to authenticate
     * @return a 200 OK Status if validated, or else a 401 Unauthorized status
     */
    public ResponseEntity<?> validateToken(String token) {
        try {
            String username = jwtUtil.getUsernameFromToken(token);
            UserDetails userDetails = userDetailServiceImp.loadUserByUsername(username);
            boolean isValid = jwtUtil.validateToken(token, userDetails);

            if (isValid) {
                return ResponseEntity.ok("Authentication successful");
            } else {
                log.error("Invalid token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
            }
        } catch (ExpiredJwtException e) {
            log.error("Token has expired: ", e);
        } catch (UsernameNotFoundException e) {
            log.error("No username found: ", e);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Token validation error: ", e);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
    }
}
