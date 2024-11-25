package com.hcc.services;

import com.hcc.dtos.AuthCredentialRequest;
import com.hcc.dtos.AuthCredentialResponse;
import com.hcc.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class LoginServiceTest {
    @Mock
    private AuthenticationManager manager;
    @Mock
    private UserDetailServiceImpl userDetailServiceImp;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private Authentication auth;
    @Mock
    private UserDetails userDetails;

    private AuthCredentialRequest request;
    private AuthCredentialResponse response;
    private final String token = "token";


    @InjectMocks
    LoginService loginService;

    @BeforeEach
    public void setup() {
        initMocks(this);

        request = new AuthCredentialRequest();
        request.setUsername("username");
        request.setPassword("password");

        response = new AuthCredentialResponse(token);
    }

    @Test
    public void login_successfulLogin_returnsOK() {
        //GIVEN
        AuthCredentialResponse expectedResponse = response;

        when(auth.getPrincipal()).thenReturn(userDetails);
        when(manager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())))
                .thenReturn(auth);
        when(jwtUtil.generateToken(userDetails)).thenReturn(token);

        //WHEN
        AuthCredentialResponse actualResponse = loginService.login(request);

        //THEN
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void login_catchesAuthenticationException_returnsUnauthorized() {
        //GIVEN
        AuthCredentialResponse expectedResponse = new AuthCredentialResponse("");
        when(manager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())))
                .thenThrow(new AuthenticationException("Invalid credentials") {});
        //WHEN
        AuthCredentialResponse actualResponse = loginService.login(request);

        //THEN
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void validateToken_isValid_returnsTrue() {
        //GIVEN
        boolean expected = true;
        String username = "username";
        boolean isValid = true;

        when(jwtUtil.getUsernameFromToken(token)).thenReturn(username);
        when(userDetailServiceImp.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken(token, userDetails)).thenReturn(isValid);

        //WHEN
        boolean actual = loginService.validateToken(token);

        //THEN
        assertEquals(expected, actual);
    }

    @Test
    public void validateToken_isNotValid_returnsFalse() {
        //GIVEN
        boolean expected = false;
        String username = "username";
        boolean isValid = false;

        when(jwtUtil.getUsernameFromToken(token)).thenReturn(username);
        when(userDetailServiceImp.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken(token, userDetails)).thenReturn(isValid);

        //WHEN
        boolean actual = loginService.validateToken(token);

        //THEN
        assertEquals(expected, actual);
    }
}
