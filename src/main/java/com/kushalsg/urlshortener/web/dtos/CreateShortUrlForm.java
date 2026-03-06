package com.kushalsg.urlshortener.web.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateShortUrlForm(
        @NotBlank(message = "Original URL is required")
        String originalUrl,
        Boolean isPrivate,
        @Min(1)
        @Max(365)
        Integer expirationInDays,
        @Size(min = 3, max = 20, message = "Alias must be between 3 and 20 characters")
        @Pattern(regexp = "^[a-zA-Z0-9-]*$", message = "Alias can only contain letters, numbers, and hyphens")
        String customAlias
) {
    public boolean isPrivateChecked() {
        return isPrivate != null && isPrivate;
    }
}