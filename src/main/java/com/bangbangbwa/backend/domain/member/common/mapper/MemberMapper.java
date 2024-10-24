package com.bangbangbwa.backend.domain.member.common.mapper;

import com.bangbangbwa.backend.domain.member.common.dto.MemberLoginDto;
import com.bangbangbwa.backend.domain.member.common.dto.MemberSignupDto;
import com.bangbangbwa.backend.domain.member.common.dto.MemberSignupDto.Request;
import com.bangbangbwa.backend.domain.member.common.entity.Member;
import com.bangbangbwa.backend.domain.token.common.TokenDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MemberMapper {

  MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);

  @Mapping(target = "nickname", source = "request.nickname")
//  @Mapping(target = "interests", source = "request.interests")
  Member dtoToEntity(Request request);

  @Mapping(target = "accessToken", source = "token.accessToken")
  @Mapping(target = "refreshToken", source = "token.refreshToken")
  MemberSignupDto.Response dtoToSignupResponse(TokenDto token);

  @Mapping(target = "accessToken", source = "token.accessToken")
  @Mapping(target = "refreshToken", source = "token.refreshToken")
  MemberLoginDto.Response dtoToLoginResponse(TokenDto token);
}
