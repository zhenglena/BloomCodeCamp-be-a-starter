package com.hcc.services;

import com.hcc.dtos.AuthCredentialRequest;
import com.hcc.dtos.AuthCredentialResponse;
import com.hcc.entities.Authority;
import com.hcc.entities.User;
import com.hcc.enums.AuthorityEnum;
import com.hcc.repositories.UserRepository;
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

import java.util.List;
import java.util.Optional;

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
    @Mock
    private UserRepository userRepo;

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
    public void login_catchesAuthenticationException_returnsEmptyString() {
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
    public void validateToken_isValid_returnsList() {
        //GIVEN
        String username = "username";
        boolean isValid = true;
        User learner = new User();
        learner.setAuthorities(List.of(new Authority(AuthorityEnum.ROLE_LEARNER.name())));

        when(jwtUtil.getUsernameFromToken(token)).thenReturn(username);
        when(userDetailServiceImp.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken(token, userDetails)).thenReturn(isValid);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(learner));

        //WHEN
        List<String> actual = loginService.validateToken(token);

        //THEN
        assertEquals("ROLE_LEARNER", actual.get(0));
    }
}
