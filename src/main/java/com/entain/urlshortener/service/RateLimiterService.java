package com.entain.urlshortener.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Service responsible for rate-limiting incoming requests.
 * It maintains a user-defined maximum number of requests allowed
 * within a specified time window to prevent overloading or misuse.
 * - Ensures that the number of requests within the defined time window
 *   does not exceed the configured limit.
 * - MAX_REQUESTS_PER_MINUTE: Defines the maximum number of requests
 *   allowed within one minute time window.
 * - TIME_WINDOW_MS: Represents the duration of the time window in milliseconds ie one minute = 60000 ms.
 * - requestTimestamps: A deque used to store the timestamps of recent requests.
 */
@Service
public class RateLimiterService {
    private static final int MAX_REQUESTS_PER_MINUTE = 100;
    private static final long TIME_WINDOW_MS = 60 * 1000;   // one minute window

    private final Deque<Long> requestTimestamps = new LinkedList<>();

    public synchronized boolean allowRequest() {
        long now = Instant.now().toEpochMilli();
        long windowStart = now - TIME_WINDOW_MS;

        while (!requestTimestamps.isEmpty() && requestTimestamps.peekFirst() < windowStart) {
            requestTimestamps.pollFirst();
        }

        if (requestTimestamps.size() < MAX_REQUESTS_PER_MINUTE) {
            requestTimestamps.addLast(now);
            return true;
        }

        return false;
    }

}
