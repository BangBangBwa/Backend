package com.bangbangbwa.backend.domain.test;

import com.bangbangbwa.backend.domain.member.business.MemberReader;
import com.bangbangbwa.backend.domain.member.common.entity.Member;
import com.bangbangbwa.backend.domain.token.common.TokenDto;
import com.bangbangbwa.backend.domain.token.service.TokenService;
import com.bangbangbwa.backend.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

  private final TokenService tokenService;
  private final MemberReader memberReader;
  @GetMapping
  public ApiResponse<?> createMember() {

    Member member = memberReader.findById(1L);


    TokenDto tokenDto = tokenService.getToken(member);
    return ApiResponse.ok(tokenDto);
  }
}
