package com.bangbangbwa.backend.domain.oauth.business;

import static com.bangbangbwa.backend.domain.oauth.business.OAuthFeignManager.BEARER;
import static com.bangbangbwa.backend.domain.oauth.business.OAuthFeignManager.CONTENT_TYPE;
import static com.bangbangbwa.backend.domain.oauth.business.OAuthFeignManager.GRANT_TYPE;

import com.bangbangbwa.backend.domain.oauth.business.feign.GoogleInfoFeign;
import com.bangbangbwa.backend.domain.oauth.common.dto.GoogleInfoDto;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GoogleInfoProvider {

  private final GoogleInfoFeign googleInfoFeign;
  private final String GOOGLE_CLIENT_ID;
  private final String GOOGLE_CLIENT_SECRET;
  private final String GOOGLE_REDIRECT_URI;
  private final String GOOGLE_GRANT_TYPE;

  GoogleInfoProvider(
      GoogleInfoFeign googleInfoFeign,
      @Value("${oauth.client.google.client-id}") String googleClientId,
      @Value("${oauth.client.google.client-secret}") String googleClientSecret,
      @Value("${oauth.client.google.redirect-url}") String googleRedirectUri
  ) {
    this.googleInfoFeign = googleInfoFeign;
    this.GOOGLE_CLIENT_ID = googleClientId;
    this.GOOGLE_CLIENT_SECRET = googleClientSecret;
    this.GOOGLE_REDIRECT_URI = googleRedirectUri;
    this.GOOGLE_GRANT_TYPE = GRANT_TYPE;
  }

  public GoogleInfoDto getInfo(String oauthToken) throws FeignException {
    return googleInfoFeign.requestInfo(BEARER + oauthToken, CONTENT_TYPE);
  }
}
