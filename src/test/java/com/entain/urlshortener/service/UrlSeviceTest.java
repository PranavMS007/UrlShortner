package com.entain.urlshortener.service;

import com.entain.urlshortener.dto.ShortenUrlRequestDto;
import com.entain.urlshortener.dto.ShortenUrlResponseDto;

import com.entain.urlshortener.dto.UrlStatsResponseDto;
import com.entain.urlshortener.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UrlSeviceTest {
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        urlService = new UrlService();
    }

    @Test
    void testCreateShortenUrlSuccess() {
        ShortenUrlRequestDto requestDto = new ShortenUrlRequestDto();
        requestDto.setUrl("https://example.com");
        ShortenUrlResponseDto responseDto = urlService.createShortenUrl(requestDto);

        assertNotNull(responseDto.getShortCode());
        assertEquals("https://example.com", responseDto.getOriginalUrl());
    }

    @Test
    void testCreateShortenUrlWithShortCode() {
        ShortenUrlRequestDto requestDto = new ShortenUrlRequestDto();
        requestDto.setUrl("https://example.com");
        requestDto.setShortCode("mycode");
        ShortenUrlResponseDto responseDto = urlService.createShortenUrl(requestDto);

        assertEquals("mycode", responseDto.getShortCode());
    }

    @Test
    void testgetOriginalUrlByShortCodeSucess() {
        ShortenUrlRequestDto requestDto = new ShortenUrlRequestDto();
        requestDto.setUrl("https://example.com");
        ShortenUrlResponseDto responseDto = urlService.createShortenUrl(requestDto);
        String originalUrl = urlService.getOriginalUrlByShortCode(responseDto.getShortCode());

        assertEquals("https://example.com", originalUrl);
    }

    @Test
    void testGetUrlVisitStatsSuccess() {
        ShortenUrlRequestDto requestDto = new ShortenUrlRequestDto();
        requestDto.setUrl("https://example.com");
        ShortenUrlResponseDto responseDto = urlService.createShortenUrl(requestDto);
        UrlStatsResponseDto urlStatsResponseDto = urlService.getUrlVisitStats(responseDto.getShortCode());

        assertEquals("https://example.com", urlStatsResponseDto.getOriginalUrl());
        assertEquals(0, urlStatsResponseDto.getVisitCount());
    }

    @Test
    void testListAllUrls() {
        ShortenUrlRequestDto shortenUrlRequestDtoOne = new ShortenUrlRequestDto();
        shortenUrlRequestDtoOne.setUrl("https://example1.com");
        ShortenUrlRequestDto shortenUrlRequestDtoTwo = new ShortenUrlRequestDto();
        shortenUrlRequestDtoTwo.setUrl("https://example2.com");
        urlService.createShortenUrl(shortenUrlRequestDtoOne);
        urlService.createShortenUrl(shortenUrlRequestDtoTwo);

        List<UrlStatsResponseDto> all = urlService.getAllShortenedUrl();
        assertEquals(2, all.size());
    }

    @Test
    void testNonExistentShortCode() {
        assertThrows(NotFoundException.class, () -> urlService.getOriginalUrlByShortCode("notfound"));
    }
}
