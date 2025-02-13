package com.bangbangbwa.backend.domain.token.business;

import com.bangbangbwa.backend.domain.member.common.entity.Member;
import com.bangbangbwa.backend.domain.token.common.dto.TokenDto;
import com.bangbangbwa.backend.domain.token.common.entity.Token;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenProvider {

  private final TokenGenerator tokenGenerator;
  private final TokenCreator tokenCreator;
  private final AuthenticationProvider authProvider;

  public TokenDto getToken(Member member) {
    Authentication authentication = authProvider.getAuthentication(member);
    TokenDto tokenDto = generateToken(authentication);
    tokenDto.setMemberInfo(member);
    saveRefreshToken(tokenDto);
    return tokenDto;
  }

  private void saveRefreshToken(TokenDto tokenDto) {
    Long refreshExp = tokenGenerator.getRefreshExp();
    Token token = Token.builder()
        .memberId(tokenDto.getMember().getId())
        .refreshToken(tokenDto.getRefreshToken())
        .expirationTime(refreshExp)
        .build();
    tokenCreator.saveRefreshToken(token);
  }

  private TokenDto generateToken(Authentication authentication) {
    String accessToken = tokenGenerator.generateAccessToken(authentication);
    String refreshToken = tokenGenerator.generateRefreshToken(authentication);

    return TokenDto.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  public Authentication getAuthentication(Claims claims) {
    return authProvider.getAuthentication(claims);
  }


}
