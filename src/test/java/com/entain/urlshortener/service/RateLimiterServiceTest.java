package com.entain.urlshortener.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RateLimiterServiceTest {
    private RateLimiterService rateLimiterService;

    @BeforeEach
    void setUp() {
        rateLimiterService = new RateLimiterService();
    }

    @Test
    void testAllowRequestUnderLimit() {
        for (int i = 0; i < 50; i++) {
            assertTrue(rateLimiterService.allowRequest());
        }
    }

    @Test
    void testAllowRequestOverLimit() {
        for (int i = 0; i < 100; i++) {
            assertTrue(rateLimiterService.allowRequest());
        }
        assertFalse(rateLimiterService.allowRequest());
    }


}
