package com.hcc.dtos;

import java.util.Objects;

public class AuthCredentialResponse {
    private String token;

    public AuthCredentialResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthCredentialResponse that = (AuthCredentialResponse) o;
        return Objects.equals(getToken(), that.getToken());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getToken());
    }
}
