package com.entain.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Data Transfer Object for URL shortening requests.
 * This class is used to carry the necessary data to perform a URL shortening operation.
 * It includes the original URL that needs to be shortened and an optional custom short code.
 *
 * Constraints:
 * - The URL field must not be blank, ensuring that a valid URL is provided for shortening.
 *
 * Usage:
 * - This DTO is typically used as the input model for APIs or services responsible for shortening URLs.
 */
@Data
public class ShortenUrlRequestDto {
    @NotBlank(message = "URL must not be blank")
    private String url;

    private String shortCode;
}
