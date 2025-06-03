package com.entain.urlshortener.controller;

import com.entain.urlshortener.dto.ShortenUrlRequestDto;
import com.entain.urlshortener.dto.ShortenUrlResponseDto;
import com.entain.urlshortener.dto.UrlStatsResponseDto;
import com.entain.urlshortener.exception.NotFoundException;
import com.entain.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

/**
 * Controller for handling URL shortening and management operations.
 * Provides endpoints for creating shortened URLs, redirecting to the
 * original URLs, retrieving URL visit statistics, and listing all stored URLs.
 */
@RestController
@RequiredArgsConstructor
public class UrlController {

    @Autowired
    private final UrlService urlService;

    /**
     * Creates a shortened URL from a given original URL.
     *
     * @param shortenUrlRequestDto DTO containing the original URL and optional custom short code
     * @return ResponseEntity containing the shortened URL details or BadRequest if URL is invalid or
     * Payload is too large if the length is greater than 2048
     */
    @PostMapping("/api/shorten")
    public ResponseEntity<ShortenUrlResponseDto> shortenUrl(@Valid @RequestBody ShortenUrlRequestDto shortenUrlRequestDto) {
        if (!urlService.isValidUrl(shortenUrlRequestDto.getUrl())) {
            return ResponseEntity.badRequest().build();
        }

        if (shortenUrlRequestDto.getUrl().length() > 2048) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body(null);
        }


        ShortenUrlResponseDto shortenUrlResponseDto = urlService.createShortenUrl(shortenUrlRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(shortenUrlResponseDto);
    }


    /**
     * Redirects to the original URL associated with the given short code.
     *
     * @param shortCode the short code to look up
     * @return RedirectView that redirects to the original URL
     * @throws NotFoundException if the short code is not found
     */
    @GetMapping("/{shortCode}")
    public RedirectView redirectToOriginalUrl(@PathVariable String shortCode) {
        String originalUrl = urlService.getOriginalUrlByShortCode(shortCode);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(originalUrl);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        return redirectView;
    }

    /**
     * Retrieves visit statistics for a shortened URL.
     *
     * @param shortCode the short code to get statistics for
     * @return ResponseEntity containing URL statistics
     * @throws NotFoundException if the short code is not found
     */
    @GetMapping("/api/stats/{shortCode}")
    public ResponseEntity<UrlStatsResponseDto> getUrlVisitStats(@PathVariable String shortCode) {
        UrlStatsResponseDto stats = urlService.getUrlVisitStats(shortCode);
        return ResponseEntity.ok(stats);
    }

    /**
     * Retrieves all shortened URLs in the system.
     *
     * @return ResponseEntity containing a list of URL details
     */
    @GetMapping("/api/urls")
    public ResponseEntity<List<UrlStatsResponseDto>> getAllUrls() {
        List<UrlStatsResponseDto> allUrls = urlService.getAllShortenedUrl();
        return ResponseEntity.ok(allUrls);
    }
}
