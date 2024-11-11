package com.hcc.exceptions;

public class UnauthorizedUpdateException extends RuntimeException {
    private static final long serialVersionUID = 478152123391593142L;

    public UnauthorizedUpdateException(String message) { super(message); }
}
