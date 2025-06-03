package com.entain.urlshortener.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a URL model used for URL shortening.
 * Contains details about the original URL, its associated short code,
 * the creation timestamp, expiry time and the visit count.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Url {
    private String shortCode;
    private String originalUrl;
    private LocalDateTime createdTime;
    private int visitCount;
    private LocalDateTime expiryTime;
}
