//package com.hcc.controllers;
//
//import com.hcc.dtos.AuthCredentialRequest;
//import com.hcc.dtos.AuthCredentialResponse;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.test.web.servlet.MockMvc;
//import com.hcc.services.LoginService;
//
//import static org.mockito.Mockito.when;
//
//@SpringBootTest
//@WebMvcTest(LoginController.class)
//public class LoginControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//    @Mock
//    AuthenticationManager manager;
//    @MockBean
//    private LoginService service;
//
//    private AuthCredentialRequest request;
//    private AuthCredentialResponse response;
//    private String token = "token";
//
//    @BeforeEach
//    void setup() {
//        request = new AuthCredentialRequest();
//        request.setUsername("username");
//        request.setPassword("password");
//
//        response = new AuthCredentialResponse(token);
//    }
//    @Test
//    public void login_returnsOK() {
//        ResponseEntity<?> okResponse = ResponseEntity.ok(response);
//        when(service.login(request)).thenReturn(okResponse);
//    }
//
//
//
//}
