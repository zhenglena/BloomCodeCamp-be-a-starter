package com.hcc.services;

import com.hcc.dtos.AuthCredentialRequest;
import com.hcc.dtos.AuthCredentialResponse;
import com.hcc.utils.JwtUtil;
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
    UserDetailServiceImpl userDetailServiceImp;
    @Autowired
    private JwtUtil jwtUtil;

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
                return ResponseEntity.ok("Token is valid");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
}
