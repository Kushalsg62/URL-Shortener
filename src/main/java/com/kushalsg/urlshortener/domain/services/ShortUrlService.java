package com.kushalsg.urlshortener.domain.services;

import com.kushalsg.urlshortener.ApplicationProperties;
import com.kushalsg.urlshortener.domain.entities.ShortUrl;
import com.kushalsg.urlshortener.domain.models.CreateShortUrlCmd;
import com.kushalsg.urlshortener.domain.models.PagedResult;
import com.kushalsg.urlshortener.domain.models.ShortUrlDto;
import com.kushalsg.urlshortener.domain.repositories.ShortUrlRepository;
import com.kushalsg.urlshortener.domain.repositories.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.kushalsg.urlshortener.domain.services.RandomUtils.generateRandomShortKey;
import static java.time.temporal.ChronoUnit.DAYS;

@Service
@Transactional(readOnly = true)
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;
    private final EntityMapper entityMapper;
    private final ApplicationProperties properties;
    private final UserRepository userRepository;

    public ShortUrlService(ShortUrlRepository shortUrlRepository,
                           EntityMapper entityMapper,
                           ApplicationProperties properties,
                           UserRepository userRepository) {
        this.shortUrlRepository = shortUrlRepository;
        this.entityMapper = entityMapper;
        this.properties = properties;
        this.userRepository = userRepository;
    }

    public PagedResult<ShortUrlDto> findAllPublicShortUrls(int pageNo, int pageSize) {
        Pageable pageable = getPageable(pageNo, pageSize);
        Page<ShortUrlDto> shortUrlDtoPage = shortUrlRepository.findPublicShortUrls(pageable)
                .map(entityMapper::toShortUrlDto);
        return PagedResult.from(shortUrlDtoPage);
    }

    public PagedResult<ShortUrlDto> getUserShortUrls(Long userId, int page, int pageSize) {
        Pageable pageable = getPageable(page, pageSize);
        var shortUrlsPage = shortUrlRepository.findByCreatedById(userId, pageable)
                .map(entityMapper::toShortUrlDto);
        return PagedResult.from(shortUrlsPage);
    }

    @Transactional
    public void deleteUserShortUrls(List<Long> ids, Long userId) {
        if (ids != null && !ids.isEmpty() && userId != null) {
            shortUrlRepository.deleteByIdInAndCreatedById(ids, userId);
        }
    }

    public PagedResult<ShortUrlDto> findAllShortUrls(int page, int pageSize) {
        Pageable pageable = getPageable(page, pageSize);
        var shortUrlsPage = shortUrlRepository.findAllShortUrls(pageable)
                .map(entityMapper::toShortUrlDto);
        return PagedResult.from(shortUrlsPage);
    }

    private Pageable getPageable(int page, int size) {
        page = page > 1 ? page - 1 : 0;
        return PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
    }

    @Transactional
    public ShortUrlDto createShortUrl(CreateShortUrlCmd cmd) {
        if (properties.validateOriginalUrl()) {
            validateUrl(cmd.originalUrl());
        }

        String shortKey;
        if (cmd.customAlias() != null && !cmd.customAlias().isBlank()) {
            shortKey = generateUniqueAliasKey(cmd.customAlias().toLowerCase().trim());
        } else {
            shortKey = generateUniqueShortKey();
        }

        var shortUrl = new ShortUrl();
        shortUrl.setOriginalUrl(cmd.originalUrl());
        shortUrl.setShortKey(shortKey);

        if (cmd.userId() == null) {
            shortUrl.setCreatedBy(null);
            shortUrl.setIsPrivate(false);
            shortUrl.setExpiresAt(Instant.now().plus(properties.defaultExpiryInDays(), DAYS));
        } else {
            shortUrl.setCreatedBy(userRepository.findById(cmd.userId()).orElseThrow());
            shortUrl.setIsPrivate(cmd.isPrivate() != null && cmd.isPrivate());
            shortUrl.setExpiresAt(cmd.expirationInDays() != null
                    ? Instant.now().plus(cmd.expirationInDays(), DAYS)
                    : null);
        }

        shortUrl.setClickCount(0L);
        shortUrl.setIsActive(true);
        shortUrl.setCreatedAt(Instant.now());
        shortUrlRepository.save(shortUrl);
        return entityMapper.toShortUrlDto(shortUrl);
    }

    /**
     * Validates the URL with specific error messages for each failure case.
     * Truncates long URLs in messages to prevent UI overflow.
     */
    private void validateUrl(String url) {
        String displayUrl = url.length() > 60 ? url.substring(0, 60) + "..." : url;

        // Check 1 — must have protocol
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new RuntimeException(
                    "Invalid URL: must start with http:// or https://");
        }

        // Check 2 — must be a valid URL format
        try {
            new java.net.URI(url).toURL();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Invalid URL format: '" + displayUrl + "' is not a properly formed URL");
        }

        // Check 3 — must actually exist and be reachable
        if (!UrlExistenceValidator.isUrlExists(url)) {
            throw new RuntimeException(
                    "URL could not be reached: '" + displayUrl + "'. " +
                            "Please check the URL is correct and the site is accessible.");
        }
    }

    @Transactional
    public Optional<ShortUrlDto> accessShortUrl(String shortKey, Long userId) {
        Optional<ShortUrl> shortUrlOptional = shortUrlRepository.findByShortKey(shortKey);

        if (shortUrlOptional.isEmpty()) {
            return Optional.empty();
        }

        ShortUrl shortUrl = shortUrlOptional.get();

        if (shortUrl.getExpiresAt() != null && shortUrl.getExpiresAt().isBefore(Instant.now())) {
            return Optional.empty();
        }

        if (shortUrl.getIsActive() != null && !shortUrl.getIsActive()) {
            return Optional.empty();
        }

        if (shortUrl.getIsPrivate() != null && shortUrl.getIsPrivate()
                && shortUrl.getCreatedBy() != null
                && !Objects.equals(shortUrl.getCreatedBy().getId(), userId)) {
            return Optional.empty();
        }

        shortUrl.setClickCount(shortUrl.getClickCount() + 1);
        shortUrlRepository.save(shortUrl);
        return Optional.of(entityMapper.toShortUrlDto(shortUrl));
    }

    @Cacheable(value = "shortUrls", key = "#shortKey")
    public Optional<ShortUrlDto> getCachedShortUrl(String shortKey) {
        return shortUrlRepository.findByShortKey(shortKey)
                .map(entityMapper::toShortUrlDto);
    }

    private String generateUniqueAliasKey(String alias) {
        String shortKey;
        do {
            shortKey = alias + "-" + generateRandomShortKey();
        } while (shortUrlRepository.existsByShortKey(shortKey));
        return shortKey;
    }

    private String generateUniqueShortKey() {
        String shortKey;
        do {
            shortKey = generateRandomShortKey();
        } while (shortUrlRepository.existsByShortKey(shortKey));
        return shortKey;
    }
}