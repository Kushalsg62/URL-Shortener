package com.kushalsg.urlshortener.domain.models;

import com.kushalsg.urlshortener.domain.entities.User;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
public record UserDto(Long id, String name) implements Serializable {
}