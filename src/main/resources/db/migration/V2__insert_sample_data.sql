INSERT INTO users (email, password, name, role)
VALUES ('admin@gmail.com', 'admin', 'Administrator', 'ROLE_ADMIN'),
       ('user@gmail.com', 'secret', 'Test User', 'ROLE_USER');

INSERT INTO short_urls (short_key, original_url, created_by, created_at, expires_at, is_private, click_count)
VALUES ('rs1Aed', 'https://github.com/kushalsg', 1, TIMESTAMP '2024-07-15', NULL, FALSE, 0),
       ('hujfDf', 'https://spring.io/projects/spring-boot', 1, TIMESTAMP '2024-07-16', NULL, FALSE, 0),
       ('ertcbn', 'https://www.postgresql.org/docs/', 1, TIMESTAMP '2024-07-17', NULL, FALSE, 0),
       ('edfrtg', 'https://docs.docker.com/', 1, TIMESTAMP '2024-07-18', NULL, TRUE, 0),
       ('vbgtyh', 'https://www.hymeleaf.org/', 1, TIMESTAMP '2024-07-19', NULL, FALSE, 0);