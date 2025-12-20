-- Remove all existing URLs and reset
TRUNCATE short_urls CASCADE;

-- Insert only clean URLs
INSERT INTO short_urls (short_key, original_url, created_by, created_at, expires_at, is_private, click_count)
VALUES ('github1', 'https://github.com/kushalsg', 1, TIMESTAMP '2024-12-15', NULL, FALSE, 0),
       ('spring1', 'https://spring.io/projects/spring-boot', 1, TIMESTAMP '2024-12-16', NULL, FALSE, 0),
       ('docs1', 'https://www.postgresql.org/docs/', 1, TIMESTAMP '2024-12-17', NULL, FALSE, 0);