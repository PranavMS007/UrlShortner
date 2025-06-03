package com.entain.urlshortener.service;

import com.entain.urlshortener.dto.ShortenUrlRequestDto;
import com.entain.urlshortener.dto.ShortenUrlResponseDto;
import com.entain.urlshortener.dto.UrlStatsResponseDto;
import com.entain.urlshortener.exception.NotFoundException;
import com.entain.urlshortener.model.Url;
import com.entain.urlshortener.util.ShortCodeGenerator;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service class responsible for managing URL shortening operations.
 * Provides functionality to create shortened URLs, track visit statistics,
 * and manage URL redirections.
 */
@Service
public class UrlService {
    private final Map<String, Url> urlDataSave = new ConcurrentHashMap<>();
    private final Map<String, String> urlShortCodeData = new ConcurrentHashMap<>();
    private static final String BASE_URL = "http://localhost:8080/";
    private static final int DEFAULT_EXPIRATION_HOURS = 24;

    /**
     * Retrieves statistics for all shortened URLs in the system.
     * @return List of UrlStatsResponseDto containing statistics for all URLs
     */
    public List<UrlStatsResponseDto> getAllShortenedUrl() {
        return urlDataSave.values().stream()
                .map(row -> UrlStatsResponseDto.builder()
                        .shortCode(row.getShortCode())
                        .originalUrl(row.getOriginalUrl())
                        .createdAt(row.getCreatedTime())
                        .visitCount(row.getVisitCount())
                        .expiryTime(row.getExpiryTime())
                        .build())
                .toList();
    }

    /**
     * Creates a shortened URL from the original URL.
     * If the URL was previously shortened, returns the existing shortened URL.
     * @param shortenUrlRequestDto DTO containing the original URL and optional custom short code
     * @return ShortenUrlResponseDto containing the shortened URL details
     * @throws IllegalArgumentException if the requested short code is already in use
     */
    public ShortenUrlResponseDto createShortenUrl(@Valid ShortenUrlRequestDto shortenUrlRequestDto) {
        String originalUrl = shortenUrlRequestDto.getUrl();

        if (urlShortCodeData.containsKey(originalUrl)) {
            String existingCode = urlShortCodeData.get(originalUrl);
            Url existing = urlDataSave.get(existingCode);
            return ShortenUrlResponseDto.builder()
                    .shortCode(existing.getShortCode())
                    .shortUrl(BASE_URL + existing.getShortCode())
                    .originalUrl(existing.getOriginalUrl())
                    .createdAt(existing.getCreatedTime())
                    .build();
        }

        String shortCode = shortenUrlRequestDto.getShortCode() != null
                ? shortenUrlRequestDto.getShortCode()
                : ShortCodeGenerator.generateCode(6);

        if (urlDataSave.containsKey(shortCode)) {
            throw new IllegalArgumentException("Short code already in use");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiringTime = now.plusHours(DEFAULT_EXPIRATION_HOURS);

        Url tempUrlData = new Url(shortCode, originalUrl, LocalDateTime.now(), 0, expiringTime);
        urlDataSave.put(shortCode, tempUrlData);
        urlShortCodeData.put(originalUrl, shortCode);

        return ShortenUrlResponseDto.builder()
                .shortCode(tempUrlData.getShortCode())
                .shortUrl(BASE_URL + tempUrlData.getShortCode())
                .originalUrl(tempUrlData.getOriginalUrl())
                .createdAt(tempUrlData.getCreatedTime())
                .build();
    }

    /**
     * Retrieves the original URL associated with a short code.
     * Increments the visit count for the URL.
     *
     * @param shortCode the short code to look up
     * @return the original URL associated with the short code
     * @throws NotFoundException if the short code is not found
     */
    public String getOriginalUrlByShortCode(String shortCode) {
        Url tempUrl = urlDataSave.get(shortCode);
        if (tempUrl == null) {
            throw new NotFoundException("Short code not found");
        }
        if (tempUrl.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new NotFoundException("This short URL has expired");
        }
        tempUrl.setVisitCount(tempUrl.getVisitCount() + 1);
        return tempUrl.getOriginalUrl();
    }

    /**
     * Retrieves statistics for a shortened URL.
     *
     * @param shortCode the short code to get statistics for
     * @return UrlStatsResponseDto containing URL statistics
     * @throws NotFoundException if the short code is not found
     */
    public UrlStatsResponseDto getUrlVisitStats(String shortCode) {
        Url tempUrl  = urlDataSave.get(shortCode);
        if (tempUrl == null) {
            throw new NotFoundException("Short code not found");
        }

        if (tempUrl.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new NotFoundException("Stats not available. This short URL has expired");
        }

        return UrlStatsResponseDto.builder()
                .shortCode(shortCode)
                .originalUrl(tempUrl.getOriginalUrl())
                .createdAt(tempUrl.getCreatedTime())
                .visitCount(tempUrl.getVisitCount())
                .expiryTime(tempUrl.getExpiryTime())
                .build();
    }

    /**
     * Validates if a given URL string is properly formatted.
     * Checks if the URL starts with http:// or https://.
     *
     * @param url the URL string to validate
     * @return true if the URL is valid, false otherwise
     */
    public boolean isValidUrl(String url) {
        return StringUtils.hasText(url) && url.matches("^(http|https)://.*$");
    }

}
