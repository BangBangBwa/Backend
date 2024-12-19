package com.bangbangbwa.backend.domain.member.service;

import com.bangbangbwa.backend.domain.comment.common.dto.MyPostCommentDto;
import com.bangbangbwa.backend.domain.member.business.FollowCreator;
import com.bangbangbwa.backend.domain.member.business.FollowDeleter;
import com.bangbangbwa.backend.domain.member.business.FollowGenerator;
import com.bangbangbwa.backend.domain.member.business.FollowReader;
import com.bangbangbwa.backend.domain.member.business.MemberCreator;
import com.bangbangbwa.backend.domain.member.business.MemberGenerator;
import com.bangbangbwa.backend.domain.member.business.MemberProvider;
import com.bangbangbwa.backend.domain.member.business.MemberReader;
import com.bangbangbwa.backend.domain.member.business.MemberUpdater;
import com.bangbangbwa.backend.domain.member.business.MemberValidator;
import com.bangbangbwa.backend.domain.member.business.NicknameProvider;
import com.bangbangbwa.backend.domain.member.common.dto.FollowDto.FollowResponse;
import com.bangbangbwa.backend.domain.member.common.dto.FollowerDto.FollowerResponse;
import com.bangbangbwa.backend.domain.member.common.dto.MemberSignupDto;
import com.bangbangbwa.backend.domain.member.common.dto.MemberUpdateDto.Request;
import com.bangbangbwa.backend.domain.member.common.dto.PostDto;
import com.bangbangbwa.backend.domain.member.common.dto.ProfileDto;
import com.bangbangbwa.backend.domain.member.common.dto.SummaryDto;
import com.bangbangbwa.backend.domain.member.common.dto.ToggleFollowDto;
import com.bangbangbwa.backend.domain.member.common.dto.TogglePostPinDto;
import com.bangbangbwa.backend.domain.member.common.entity.Follow;
import com.bangbangbwa.backend.domain.member.common.entity.Member;
import com.bangbangbwa.backend.domain.member.common.enums.Role;
import com.bangbangbwa.backend.domain.oauth.common.dto.OAuthInfoDto;
import com.bangbangbwa.backend.domain.post.business.PostReader;
import com.bangbangbwa.backend.domain.post.business.PostValidator;
import com.bangbangbwa.backend.domain.promotion.business.StreamerReader;
import com.bangbangbwa.backend.domain.sns.business.PostUpdater;
import com.bangbangbwa.backend.domain.tag.business.TagRelation;
import com.bangbangbwa.backend.domain.tag.business.TagUpdater;
import com.bangbangbwa.backend.domain.tag.common.dto.TagDto;
import com.bangbangbwa.backend.domain.token.business.TokenProvider;
import com.bangbangbwa.backend.domain.token.common.dto.TokenDto;
import com.bangbangbwa.backend.global.util.S3Manager;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberGenerator memberGenerator;
  private final MemberValidator memberValidator;
  private final MemberCreator memberCreator;
  private final MemberReader memberReader;
  private final TokenProvider tokenProvider;
  private final NicknameProvider nicknameProvider;
  private final MemberProvider memberProvider;
  private final StreamerReader streamerReader;
  private final FollowReader followReader;
  private final PostReader postReader;
  private final FollowGenerator followGenerator;
  private final FollowCreator followCreator;
  private final FollowDeleter followDeleter;
  private final PostValidator postValidator;
  private final PostUpdater postUpdater;
  private final MemberUpdater memberUpdater;
  private final S3Manager s3Manager;
  private final TagRelation tagRelation;
  private final TagUpdater tagUpdater;

  @Transactional
  public TokenDto signup(
      OAuthInfoDto oAuthInfo,
      MemberSignupDto.Request request,
      MultipartFile profileFile
  ) {
    memberValidator.validateUniqueMember(oAuthInfo);
    Member member = memberGenerator.generate(oAuthInfo, request);
    if (Objects.nonNull(profileFile)) {
      String profileImage = s3Manager.upload(profileFile);
      member.updateProfile(profileImage);
    }
    memberCreator.save(member);
    return tokenProvider.getToken(member);
  }

  public TokenDto login(OAuthInfoDto oAuthInfo) {
    Member member = memberReader.findBySns(oAuthInfo);
    return tokenProvider.getToken(member);
  }

  public void checkNickname(String nickname) {
    memberValidator.validateNicknameDuplication(nickname);
  }

  public String serveRandomNickname() {
    Set<String> nicknames = nicknameProvider.provideRandomNicknames(1);
    if (nicknames.iterator().hasNext()) {
      return nicknames.iterator().next();
    }
    return null;
  }

  public Boolean isMyMemberId(Long memberId) {
    return memberValidator.isMyMemberId(memberId);
  }

  public ProfileDto getProfile(Long memberId) {
    Long currentMemberId = memberProvider.getCurrentMemberIdOrNull();
    ProfileDto profileDto = new ProfileDto(memberId, currentMemberId);
    return memberReader.getProfile(profileDto);
  }

  public SummaryDto getSummary(Long memberId) {
    Long currentMemberId = memberProvider.getCurrentMemberIdOrNull();
    SummaryDto request = new SummaryDto(memberId, currentMemberId);

    SummaryDto summaryDto = memberReader.getSummary(request);

    if (Objects.equals(memberProvider.getCurrentRoleOrNull(), Role.STREAMER)) {
      summaryDto.setPlatforms(streamerReader.findStreamerPlatforms(memberId));
    }

    if (!Objects.equals(memberId, currentMemberId)) {
      memberValidator.removeData(summaryDto);
    }
    return summaryDto;
  }

  public List<PostDto> getPosts(Long memberId) {
    return postReader.findPostsByMemberId(memberId);
  }

  public MyPostCommentDto getComments(Long memberId) {
    memberReader.findById(memberId);
    return null;
  }

  public List<FollowerResponse> getFollowers(Long memberId) {
    return followReader.findFollowersByMemberId(memberId);
  }

  public List<FollowResponse> getFollows(Long memberId) {
    return followReader.findFollowsByMemberId(memberId);
  }

  public void toggleFollow(ToggleFollowDto.Request req) {
    Long memberId = memberProvider.getCurrentMemberId();
    Follow follow = followGenerator.generate(req, memberId);
    if (req.isFollow()) {
      followCreator.save(follow);
    } else {
      followDeleter.deleteByFollowerIdAndFollowId(memberId, req.memberId());
    }
  }

  public void togglePostPin(TogglePostPinDto.Request req) {
    Long memberId = memberProvider.getCurrentMemberId();
    postValidator.validatePostWriter(req.postId(), memberId);
    if (req.pinned()) {
      postValidator.validatePostPinLimit(memberId);
    }
    postUpdater.updatePostPin(req.postId(), req.pinned());
  }

  @Transactional
  public ProfileDto updateMember(
      List<TagDto> tagList,
      Request request,
      MultipartFile file
  ) {
    Member member = memberReader.findById(memberProvider.getCurrentMemberId());

    // 프로필 갱신
    if (file != null) {
      String newProfile = s3Manager.upload(file);
      member.updateProfile(newProfile);
    }

    // 회원 정보 갱신 - 닉네임 중복여부 처리
    String newNickname = request.nickname();
    String newSelfIntroduction = request.selfIntroduction();
    if (!member.getNickname().equals(newNickname)) {
      memberValidator.validateNicknameDuplication(newNickname);
    }
    member.updateInfo(newNickname, newSelfIntroduction);
    memberUpdater.updateMember(member);

    // 태그목록 갱신 - 일반회원/스트리머 구분하여 변경.
    tagUpdater.updateTagRelations(member, tagList);

    // 프로필 DTO 변환
    return memberReader.getProfile(new ProfileDto(member.getId(), member.getId()));
  }

  public void memberTagRelation(Member member, List<TagDto> tagList) {
    tagRelation.relation(member, tagList);
  }
}