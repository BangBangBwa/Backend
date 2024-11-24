package com.bangbangbwa.backend.domain.member.business;

import com.bangbangbwa.backend.domain.member.common.entity.Member;
import com.bangbangbwa.backend.domain.member.exception.NotSignupMemberException;
import com.bangbangbwa.backend.domain.member.exception.EmptyNicknameException;
import com.bangbangbwa.backend.domain.member.repository.MemberRepository;
import com.bangbangbwa.backend.domain.oauth.common.dto.OAuthInfoDto;
import com.bangbangbwa.backend.domain.oauth.common.enums.SnsType;
import com.bangbangbwa.backend.domain.member.exception.NotFoundMemberException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberReader {

  private final MemberRepository memberRepository;

  public Member findBySns(OAuthInfoDto oAuthInfo) {
    String snsId = oAuthInfo.getSnsId();
    SnsType snsType = oAuthInfo.getSnsType();
    return memberRepository.findBySns(snsId, snsType).orElseThrow(
        () -> new NotSignupMemberException(oAuthInfo.getOAuthToken())
    );
  }
  
  public Member findById(Long memberId) {
    return memberRepository.findById(memberId).orElseThrow(NotFoundMemberException::new);
  }

  public boolean existsByNickname(String nickname) {
    return memberRepository.isExistsNickname(nickname);
  }

  public List<Member> findByNicknameContaining(String nickname) {
    if (nickname == null || nickname.trim().isEmpty()) throw new EmptyNicknameException();
    return memberRepository.findByNicknameContaining(nickname);
  }
}
