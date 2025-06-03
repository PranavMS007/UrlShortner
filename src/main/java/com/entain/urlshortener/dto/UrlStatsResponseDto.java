package com.entain.urlshortener.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for URL statistics.
 * This class is used to represent various details of a shortened URL,
 * including its short code, original URL, the creation timestamp, and
 * the number of times the URL has been visited.
 *
 * It is primarily used to respond to requests for retrieving statistics
 * about shortened URLs, such as identifying the total visit count and
 * other associated details.
 */
@Data
@Builder
public class UrlStatsResponseDto {
    private String shortCode;
    private String originalUrl;
    private LocalDateTime createdAt;
    private int visitCount;
    private LocalDateTime expiryTime;

}
