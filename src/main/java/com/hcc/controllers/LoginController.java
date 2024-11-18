package com.hcc.controllers;

import com.hcc.dtos.AuthCredentialRequest;
import com.hcc.services.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class LoginController {
    @Autowired
    LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthCredentialRequest request) {
        return loginService.login(request);
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        return loginService.validateToken(token);
    }
}
