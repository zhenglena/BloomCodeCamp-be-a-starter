package com.hcc.controllers;

import com.hcc.dtos.AuthCredentialRequest;
import com.hcc.dtos.AuthCredentialResponse;
import com.hcc.services.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class LoginController {
    @Autowired
    LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthCredentialRequest request) {
        AuthCredentialResponse response = loginService.login(request);
        if (response.getToken().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(response.getToken());
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        List<String> authorityList = loginService.validateToken(token);

        if (!authorityList.isEmpty()) {
            return ResponseEntity.ok(authorityList.get(0));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }
    }
}
