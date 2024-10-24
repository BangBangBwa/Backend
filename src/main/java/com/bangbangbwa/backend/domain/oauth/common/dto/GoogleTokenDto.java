package com.bangbangbwa.backend.domain.oauth.common.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GoogleTokenDto(
    String accessToken,
    String idToken
) {

}
