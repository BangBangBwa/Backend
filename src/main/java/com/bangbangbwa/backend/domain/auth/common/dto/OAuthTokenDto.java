package com.bangbangbwa.backend.domain.auth.common.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

public record OAuthTokenDto(
    @JsonProperty("access_token")
    String accessToken
){ }
