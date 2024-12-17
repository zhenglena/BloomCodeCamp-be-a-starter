package com.hcc.exceptions;

public class UnauthorizedAccessException extends RuntimeException {
    private static final long serialVersionUID = -3736098389802603728L;

    public UnauthorizedAccessException(String message) { super(message); }
}
