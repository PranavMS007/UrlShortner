package com.entain.urlshortener.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for the response of a URL shortening operation.
 * This class encapsulates details about a shortened URL including its
 * short URL representation, short code, original URL, and the creation timestamp.
 * It is used as a response model when clients request the creation of a shortened URL.
 */
@Data
@Builder
public class ShortenUrlResponseDto {
    private String shortUrl;
    private String shortCode;
    private String originalUrl;
    private LocalDateTime createdAt;
}
