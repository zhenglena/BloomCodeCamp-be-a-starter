package com.hcc.services;

import com.hcc.TestHelper;
import com.hcc.dtos.AuthCredentialRequest;
import com.hcc.dtos.AuthCredentialResponse;
import com.hcc.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private ResponseEntity<?> expectedResponse;
    private ResponseEntity<?> actualResponse;


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
        expectedResponse = ResponseEntity.ok(response);

        when(auth.getPrincipal()).thenReturn(userDetails);
        when(manager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())))
                .thenReturn(auth);
        when(jwtUtil.generateToken(userDetails)).thenReturn(token);

        //WHEN
        actualResponse = loginService.login(request);

        //THEN
        TestHelper.testResponseEntity(expectedResponse, actualResponse);
    }

    @Test
    public void login_throwsAuthenticationException_returnsUnauthorized() {
        //GIVEN
        expectedResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        when(manager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())))
                .thenThrow(new AuthenticationException("Invalid credentials") {});
        //WHEN
        actualResponse = loginService.login(request);

        //THEN
        TestHelper.testResponseEntity(expectedResponse, actualResponse);
    }

    @Test
    public void validateToken_isValid_returnsOK() {
        //GIVEN
        expectedResponse = ResponseEntity.ok("Authentication successful");
        String username = "username";
        boolean isValid = true;

        when(jwtUtil.getUsernameFromToken(token)).thenReturn(username);
        when(userDetailServiceImp.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken(token, userDetails)).thenReturn(isValid);

        //WHEN
        actualResponse = loginService.validateToken(token);

        //THEN
        TestHelper.testResponseEntity(expectedResponse, actualResponse);
    }

    @Test
    public void validateToken_isNotValid_returnsUnauthorized() {
        //GIVEN
        expectedResponse =
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        String username = "username";
        boolean isValid = false;

        when(jwtUtil.getUsernameFromToken(token)).thenReturn(username);
        when(userDetailServiceImp.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken(token, userDetails)).thenReturn(isValid);

        //WHEN
        actualResponse = loginService.validateToken(token);

        //THEN
        TestHelper.testResponseEntity(expectedResponse, actualResponse);
    }

    @Test
    public void validateToken_expiredJwtException_returnsUnauthorized() {
        //GIVEN
        expectedResponse =
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        when(jwtUtil.getUsernameFromToken(token)).thenThrow(new ExpiredJwtException(null, null, "Token expired"));

        //WHEN
        actualResponse = loginService.validateToken(token);

        //THEN
        TestHelper.testResponseEntity(expectedResponse, actualResponse);
    }

    @Test
    public void validateToken_usernameNotFoundException_returnsUnauthorized() {
        //GIVEN
        expectedResponse =
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        when(jwtUtil.getUsernameFromToken(token)).thenThrow(new UsernameNotFoundException("No username found with " +
                "token: " + token));

        //WHEN
        actualResponse = loginService.validateToken(token);

        //THEN
        TestHelper.testResponseEntity(expectedResponse, actualResponse);
    }

    @Test
    public void validateToken_illegalArgumentException_returnsUnauthorized() {
        //GIVEN
        expectedResponse =
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        when(jwtUtil.getUsernameFromToken(token)).thenThrow(new IllegalArgumentException());

        //WHEN
        actualResponse = loginService.validateToken(token);

        //THEN
        TestHelper.testResponseEntity(expectedResponse, actualResponse);
    }
}
