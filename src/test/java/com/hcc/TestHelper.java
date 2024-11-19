package com.hcc;

import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestHelper {
    public static void testResponseEntity(ResponseEntity<?> expected, ResponseEntity<?> actual) {
        assertEquals(expected.getStatusCode(), actual.getStatusCode());
        assertEquals(expected.getBody(), actual.getBody());
    }
}
