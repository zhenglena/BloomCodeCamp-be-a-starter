package com.hcc.dtos;

public class AuthCredentialResponse {
    private String token;

    public AuthCredentialResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
