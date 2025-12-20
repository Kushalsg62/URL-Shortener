# Spring Boot URL Shortener

URL Shortener is a service that shortens a long URL into a short URL which is easier to share.

## Prerequisites
* Java 21
* Docker

## Tech Stack
* Spring Boot
* Spring Security
* Spring Data JPA
* PostgreSQL
* FlywayDb Migrations
* Thymeleaf
* Bootstrap CSS

```shell
# To build Docker image with default name sivaprasadreddy/spring-boot-url-shortener
$  ./mvnw spring-boot:build-image

# To build Docker image with custom name
$  ./mvnw spring-boot:build-image -DdockerImageName=kushalsg/spring-boot-url-shortener

# To run the application using Docker Compose
$ cd docker
$ docker compose up -d
```