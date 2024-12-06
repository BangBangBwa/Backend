package com.bangbangbwa.backend.domain.sns.service;

import com.bangbangbwa.backend.domain.member.business.MemberProvider;
import com.bangbangbwa.backend.domain.member.business.MemberReader;
import com.bangbangbwa.backend.domain.member.business.MemberValidator;
import com.bangbangbwa.backend.domain.member.common.entity.Member;
import com.bangbangbwa.backend.domain.member.common.enums.Role;
import com.bangbangbwa.backend.domain.sns.business.*;
import com.bangbangbwa.backend.domain.sns.common.dto.*;
import com.bangbangbwa.backend.domain.sns.common.entity.*;
import com.bangbangbwa.backend.domain.sns.common.enums.PostType;
import com.bangbangbwa.backend.domain.sns.common.enums.VisibilityType;
import com.bangbangbwa.backend.domain.streamer.common.business.DailyMessageReader;
import com.bangbangbwa.backend.domain.streamer.common.entity.DailyMessage;
import com.bangbangbwa.backend.global.util.S3Manager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class SnsService {

  private final MemberProvider memberProvider;
  private final MemberValidator memberValidator;
  private final MemberReader memberReader;
  private final PostGenerator postGenerator;
  private final PostCreator postCreator;
  private final PostReader postReader;
  private final PostProvider postProvider;
  private final PostValidator postValidator;
  private final PostVisibilityMemberCreator postVisibilityMemberCreator;
  private final PostVisibilityMemberGenerator postVisibilityMemberGenerator;
  private final S3Manager s3Manager;
  private final CommentGenerator commentGenerator;
  private final CommentCreator commentCreator;
  private final PostTypeProvider postTypeProvider;
  private final ReportValidator reportValidator;
  private final ReportPostCreator reportPostCreator;
  private final ReportPostGenerator reportPostGenerator;
  private final ReportCommentGenerator reportCommentGenerator;
  private final ReportCommentCreator reportCommentCreator;
  private final DailyMessageReader dailyMessageReader;
  private final ReaderPostReader readerPostReader;
  private final ReaderPostCreator readerPostCreator;

  // 게시글 저장 전, content에서 url을 추출 후 redis 값 삭제하는 과정 필요
  @Transactional
  public Post createPost(CreatePostDto.Request request) {
    Member member = memberProvider.getCurrentMember();
    PostType postType = request.postType();
    memberValidator.validateRole(member.getRole(), postType);

    Post post = postGenerator.generate(request, member);
    postCreator.save(post);

    VisibilityType type = postValidator.validateMembers(
        request.publicMembers(),
        request.privateMembers()
    );

    if (type != null) {
      List<Long> memberList = (type == VisibilityType.PRIVATE) ?
          request.privateMembers() : request.publicMembers();

      List<PostVisibilityMember> postVisibilityMember = postVisibilityMemberGenerator.generate(
          post, type, memberList
      );

      postVisibilityMemberCreator.saveList(postVisibilityMember);
    }

    return post;
  }

  public String uploadPostMedia(MultipartFile file) {
    return s3Manager.upload(file);
  }


  public Comment createComment(CreateCommentDto.Request request) {
    Member member = memberProvider.getCurrentMember();
    postReader.findById(request.postId());
    Comment comment = commentGenerator.generate(request, member);
    commentCreator.save(comment);
    return comment;
  }

  public List<Member> searchMember(String nickname) {
    return memberReader.findByNicknameContaining(nickname);
  }

  public GetPostDetailsDto.Response getPostDetails(Long postId) {
    Long memberId = memberProvider.getCurrentMemberIdOrNull();
    GetPostDetailsDto.Response response = postReader.getPostDetails(postId, memberId);
    if (memberId != null) readerPostCreator.addReadPost(memberId, response.postId());
    return response;
  }

  // note. streamer 계정이 조회했을 때의 작업 필요
  public List<Post> getPostList() {
    Role role = memberProvider.getCurrentRole();
    PostType postType = postTypeProvider.getInversePostTypeForRole(role);

    if (role == Role.GUEST) { return postProvider.getRandomPost(postType); }
    if (role == Role.STREAMER) { return postProvider.getRandomPost(postType); }
    Long memberId = memberProvider.getCurrentMemberId();
    Set<String> readPostIds = readerPostReader.findAllReadPostsByMemberId(memberId);
    return postProvider.getMemberPersonalizedPosts(memberId, readPostIds);
  }

  public List<GetLatestPostsDto.Response> getLatestPosts(PostType postType) {
    Set<String> readerPostList = new HashSet<>();
    Long memberId = memberProvider.getCurrentMemberIdOrNull();
    if (memberId != null) readerPostList = readerPostReader.findAllReadPostsByMemberId(memberId);

    List<GetLatestPostsDto> getLatestPostsDto = postReader.findPostsWithinLast24Hours(postType, readerPostList);
    List<Long> memberIds = getLatestPostsDto.stream()
            .map(GetLatestPostsDto::getMemberId)
            .toList();

    List<DailyMessage> dailyMessageList = dailyMessageReader.findByIds(memberIds);

      return IntStream.range(0, getLatestPostsDto.size())
              .mapToObj(i -> getLatestPostsDto.get(i).toResponse(dailyMessageList.get(i)))
              .collect(Collectors.toList());
  }

  public void reportPost(ReportPostDto.Request request) {
    Long memberId = memberProvider.getCurrentMemberId();
    reportValidator.checkForDuplicateReportPost(request.postId(), memberId);
    ReportPost reportPost = reportPostGenerator.generate(request, memberId);
    reportPostCreator.save(reportPost);
  }

  public void reportComment(ReportCommentDto.Request request) {
    Long memberId = memberProvider.getCurrentMemberId();
    reportValidator.checkForDuplicateReportComment(request.commentId(), memberId);
    ReportComment reportComment = reportCommentGenerator.generate(request, memberId);
    reportCommentCreator.save(reportComment);
  }
}
