package com.hcc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcc.dtos.AuthCredentialRequest;
import com.hcc.dtos.AuthCredentialResponse;
import com.hcc.services.LoginService;
import com.hcc.services.UserDetailServiceImpl;
import com.hcc.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoginService loginService;
    @MockBean
    private AuthenticationManager manager;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private UserDetailServiceImpl userDetailService;

    @Test
    public void login_successful_returnsOK() throws Exception {
        //GIVEN
        AuthCredentialRequest request = new AuthCredentialRequest();
        request.setUsername("user");
        request.setPassword("pass");
        String token = "mockToken";
        AuthCredentialResponse response = new AuthCredentialResponse(token);

        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(auth.getPrincipal()).thenReturn(userDetails);
        when(manager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtUtil.generateToken(userDetails)).thenReturn(token);
        when(loginService.login(any(AuthCredentialRequest.class))).thenReturn(response);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/auth/login")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Successful login"));

        verify(loginService).login(any(AuthCredentialRequest.class));
    }

    @Test
    public void login_unsuccessful_returnsUnauthorized() throws Exception {
        AuthCredentialRequest request = new AuthCredentialRequest();
        request.setUsername("user");
        request.setPassword("pass");
        String token = "";
        AuthCredentialResponse response = new AuthCredentialResponse(token);

        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(auth.getPrincipal()).thenReturn(userDetails);
        when(manager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtUtil.generateToken(userDetails)).thenReturn(token);
        when(loginService.login(any(AuthCredentialRequest.class))).thenReturn(response);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/login")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid login"));

        verify(loginService).login(any(AuthCredentialRequest.class));
    }

    @Test
    public void validateToken_successfulAuthentication_returnsOK() throws Exception {
        String token = "mockToken";
        String username = "username";
        boolean isValid = true;

        UserDetails userDetails = mock(UserDetails.class);

        when(jwtUtil.getUsernameFromToken(token)).thenReturn(username);
        when(userDetailService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken(token, userDetails)).thenReturn(isValid);
        when(loginService.validateToken(token)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/auth/validate")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().string("Authentication successful"));

        verify(loginService).validateToken(token);
    }

    @Test
    public void validateToken_unsuccessfulAuthentication_returnsUnauthorized() throws Exception {
        String token = "mockToken";
        String username = "username";
        boolean isValid = false;

        UserDetails userDetails = mock(UserDetails.class);

        when(jwtUtil.getUsernameFromToken(token)).thenReturn(username);
        when(userDetailService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken(token, userDetails)).thenReturn(isValid);
        when(loginService.validateToken(token)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/validate")
                        .header("Authorization", token))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Authentication failed"));

        verify(loginService).validateToken(token);
    }

}
