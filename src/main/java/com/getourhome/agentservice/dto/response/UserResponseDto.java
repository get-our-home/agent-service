package com.getourhome.agentservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDto {
    @JsonProperty("jwt")
    private String jwt;

    private String role;
}