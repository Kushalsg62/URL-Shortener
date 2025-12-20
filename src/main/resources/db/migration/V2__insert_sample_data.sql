DELETE FROM short_urls;
DELETE FROM users WHERE email NOT IN ('admin@gmail.com', 'user@gmail.com');

INSERT INTO users (email, password, name, role)
VALUES ('admin@gmail.com', 'admin', 'Administrator', 'ROLE_ADMIN'),
       ('user@gmail.com', 'secret', 'Test User', 'ROLE_USER')
ON CONFLICT (email) DO NOTHING;

INSERT INTO short_urls (short_key, original_url, created_by, created_at, expires_at, is_private, click_count)
VALUES ('github1', 'https://github.com/kushalsg', 1, TIMESTAMP '2024-12-15', NULL, FALSE, 0),
       ('spring1', 'https://spring.io/projects/spring-boot', 1, TIMESTAMP '2024-12-16', NULL, FALSE, 0),
       ('docs1', 'https://www.postgresql.org/docs/', 1, TIMESTAMP '2024-12-17', NULL, FALSE, 0);