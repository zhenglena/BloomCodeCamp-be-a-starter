package com.hcc.exceptions;

public class ResourceNotFoundException extends RuntimeException{

    private static final long serialVersionUID = -57176704897102456L;

    public ResourceNotFoundException(String message) {
        super(message);
    }
}