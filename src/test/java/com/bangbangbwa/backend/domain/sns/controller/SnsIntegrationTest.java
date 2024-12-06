package com.bangbangbwa.backend.domain.sns.controller;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static com.mongodb.assertions.Assertions.assertNull;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.bangbangbwa.backend.domain.member.common.entity.Follow;
import com.bangbangbwa.backend.domain.member.common.entity.Member;

import com.bangbangbwa.backend.domain.member.common.enums.Role;
import com.bangbangbwa.backend.domain.member.exception.UnAuthenticationMemberException;
import com.bangbangbwa.backend.domain.member.repository.FollowRepository;
import com.bangbangbwa.backend.domain.member.repository.MemberRepository;
import com.bangbangbwa.backend.domain.oauth.common.dto.OAuthInfoDto;
import com.bangbangbwa.backend.domain.oauth.common.enums.SnsType;
import com.bangbangbwa.backend.domain.sns.common.dto.*;
import com.bangbangbwa.backend.domain.sns.common.entity.Comment;
import com.bangbangbwa.backend.domain.sns.common.entity.Post;
import com.bangbangbwa.backend.domain.sns.common.entity.ReportComment;
import com.bangbangbwa.backend.domain.sns.common.entity.ReportPost;
import com.bangbangbwa.backend.domain.sns.common.enums.PostType;
import com.bangbangbwa.backend.domain.sns.common.enums.ReportStatus;
import com.bangbangbwa.backend.domain.sns.exception.DuplicateReportException;
import com.bangbangbwa.backend.domain.sns.exception.InvalidMemberVisibilityException;
import com.bangbangbwa.backend.domain.sns.repository.*;
import com.bangbangbwa.backend.domain.sns.exception.NotFoundPostException;
import com.bangbangbwa.backend.domain.streamer.repository.DailyMessageRepository;
import com.bangbangbwa.backend.domain.token.business.TokenProvider;
import com.bangbangbwa.backend.domain.token.common.TokenDto;
import com.bangbangbwa.backend.global.response.ApiResponse;
import com.bangbangbwa.backend.global.test.IntegrationTest;
import com.bangbangbwa.backend.global.util.S3Manager;
import com.bangbangbwa.backend.global.util.randomValue.Language;
import com.bangbangbwa.backend.global.util.randomValue.RandomValue;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

class SnsIntegrationTest extends IntegrationTest {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private FollowRepository followRepository;

  @Autowired
  private ReportPostRepository reportPostRepository;

  @Autowired
  private ReportCommentRepository reportCommentRepository;

  @Autowired
  private DailyMessageRepository dailyMessageRepository;

  @Autowired
  private ReaderPostRepository readerPostRepository;

  @Autowired
  private TokenProvider tokenProvider;

  @MockBean
  private S3Manager s3Manager;

  private Member getMember() {
    Member member = Member.builder()
        .nickname(RandomValue.string(255).setNullable(false).get())
        .build();
    OAuthInfoDto oAuthInfo = OAuthInfoDto.builder()
        .snsId(RandomValue.string(255).setNullable(false).get())
        .email(RandomValue.string(255).setNullable(false).getEmail())
        .snsType(RandomValue.getRandomEnum(SnsType.class))
        .build();
    member.addOAuthInfo(oAuthInfo);
    member.updateProfile(RandomValue.string(255).get());

    return member;
  }

  private Member createMember() {
    Member member = getMember();
    memberRepository.save(member);
    return member;
  }

  private Post getPost(PostType postType, Member member) {
    return Post.builder()
        .postType(postType)
        .memberId(member.getId())
        .title(RandomValue.string(100).setNullable(false).get())
        .content(RandomValue.string(2000).setNullable(false).get())
        .build();
  }

  private Post createPost(PostType postType, Member writeMember) {
    Post post = getPost(postType, writeMember);
    postRepository.save(post);
    return post;
  }

  private Follow getFollow(Long followerId, Long followeeId) {
    return Follow.builder()
            .followerId(followerId)
            .followeeId(followeeId)
            .build();
  }

  private Follow createFollow(Member followerId, Member followeeId) {
    Follow follow = getFollow(followerId.getId(), followeeId.getId());
    followRepository.save(follow);
    return follow;
  }

  private ReportPost getReportPost(Long postId, String memberId) {
    return ReportPost.builder()
            .createdId(memberId)
            .postId(postId)
            .build();
  }

  private ReportPost createReportPost(Post post, Member member) {
    ReportPost reportPost = getReportPost(post.getId() , member.getId().toString());
    reportPostRepository.save(reportPost);
    return reportPost;
  }

  private Comment getComment(Post post, Member member) {
    return Comment.builder()
            .content(RandomValue.string(30).setNullable(false).get())
            .memberId(member.getId())
            .postId(post.getId())
            .build();
  }

  private Comment createComment(Post post, Member member) {
    Comment comment = getComment(post, member);
    commentRepository.save(comment);
    return comment;
  }

  private ReportComment getReportComment(Comment comment, Member member) {
    return ReportComment.builder()
            .commentId(comment.getId())
            .createdId(member.getId().toString())
            .build();
  }

  private ReportComment createReportComment(Comment comment, Member member) {
    ReportComment reportComment = getReportComment(comment, member);
    reportCommentRepository.save(reportComment);
    return reportComment;
  }


  // note. 이미지/동영상 여부 값 비교 필요
  @Test
  void getPostList_토큰_보유() {
    // given
    Role memberRole = (RandomValue.getInt(2) == 1) ? Role.MEMBER : Role.STREAMER;
    PostType postType = (memberRole == Role.MEMBER) ? PostType.STREAMER : PostType.MEMBER;

    Member member = getMember();
    member.updateRole(memberRole);
    memberRepository.save(member);

    TokenDto tokenDto = tokenProvider.getToken(member);

    Member writeMember = createMember();
    int postCount = RandomValue.getInt(0,5);

    List<Post> postList = IntStream.range(0, postCount)
            .mapToObj(i -> createPost(postType, writeMember))
            .toList();


    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(tokenDto.getAccessToken());
    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

    String url = "http://localhost:" + port + "/api/v1/sns/getPostList";


    // when
    ResponseEntity<String> responseEntity = restTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            String.class
    );

    ApiResponse<List<GetPostListDto.Response>> apiResponse = gson.fromJson(
        responseEntity.getBody(),
        new TypeToken<ApiResponse<List<GetPostListDto.Response>>>() {}.getType()
    );

    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertNotNull(apiResponse.getData());
    assertThat(apiResponse.getData().size()).isEqualTo(postCount);


    Comparator<GetPostListDto.Response> getPostId = Comparator.comparing(GetPostListDto.Response::postId);
    List<GetPostListDto.Response> actualList = apiResponse.getData().stream().sorted(getPostId).toList();

    List<GetPostListDto.Response> expectedList = postList.stream()
            .map(post -> new GetPostListDto.Response(
                    post.getId(),
                    post.getTitle(),
                    false,
                    false
                    )
            ).sorted(getPostId).toList();

    assertThat(actualList).usingRecursiveComparison().isEqualTo(expectedList);
  }

  @Test
  void getPostList_토큰_미입력() {
    // given
    PostType postType = PostType.STREAMER;

    Member writeMember = createMember();
    int postCount = RandomValue.getInt(0,5);

    IntStream.range(0, postCount)
            .forEach(i -> createPost(postType, writeMember));


    String url = "http://localhost:" + port + "/api/v1/sns/getPostList";


    // when
    ResponseEntity<String> responseEntity = restTemplate.getForEntity(
            url,
            String.class
    );

    ApiResponse<List<GetPostListDto.Response>> apiResponse = gson.fromJson(
            responseEntity.getBody(),
            new TypeToken<ApiResponse<List<GetPostListDto.Response>>>() {}.getType()
    );

    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertNotNull(apiResponse.getData());
    assertThat(apiResponse.getData().size()).isEqualTo(postCount);

  }



  @Test
  void getPostDetails() {
    // given
    Member member = createMember();
    TokenDto tokenDto = tokenProvider.getToken(member);

    Member writeMember = createMember();
    PostType postType = RandomValue.getRandomEnum(PostType.class);
    Post post = createPost(postType, writeMember);

    boolean isFollow = RandomValue.getRandomBoolean();
    if (isFollow) { createFollow(member, writeMember); }

    String url = "http://localhost:" + port + "/api/v1/sns/getPostDetails/" + post.getId();

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(tokenDto.getAccessToken());
    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

    // when
    ResponseEntity<String> responseEntity = restTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            String.class
    );

    ApiResponse<GetPostDetailsDto.Response> apiResponse = gson.fromJson(
        responseEntity.getBody(),
        new TypeToken<ApiResponse<GetPostDetailsDto.Response>>() {}.getType()
    );

    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertNotNull(apiResponse.getData());
    assertThat(apiResponse.getData().postId()).isEqualTo(post.getId());
    assertThat(apiResponse.getData().writerId()).isEqualTo(writeMember.getId());
    assertThat(apiResponse.getData().title()).isEqualTo(post.getTitle());
    assertThat(apiResponse.getData().nickname()).isEqualTo(writeMember.getNickname());
    assertThat(apiResponse.getData().profileUrl()).isEqualTo(writeMember.getProfile());
    assertThat(apiResponse.getData().content()).isEqualTo(post.getContent());
    assertThat(apiResponse.getData().isFollowed()).isEqualTo(isFollow);

  }

  @Test
  void getPostDetails_토큰_미입력() {
    // given
    Member writeMember = createMember();
    PostType postType = RandomValue.getRandomEnum(PostType.class);
    Post post = createPost(postType, writeMember);

    String url = "http://localhost:" + port + "/api/v1/sns/getPostDetails/" + post.getId();

    // when
    ResponseEntity<String> responseEntity = restTemplate.getForEntity(
            url,
            String.class
    );

    ApiResponse<GetPostDetailsDto.Response> apiResponse = gson.fromJson(
            responseEntity.getBody(),
            new TypeToken<ApiResponse<GetPostDetailsDto.Response>>() {}.getType()
    );

    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertNotNull(apiResponse.getData());
    assertThat(apiResponse.getData().postId()).isEqualTo(post.getId());
    assertThat(apiResponse.getData().writerId()).isEqualTo(writeMember.getId());
    assertThat(apiResponse.getData().title()).isEqualTo(post.getTitle());
    assertThat(apiResponse.getData().nickname()).isEqualTo(writeMember.getNickname());
    assertThat(apiResponse.getData().profileUrl()).isEqualTo(writeMember.getProfile());
    assertThat(apiResponse.getData().content()).isEqualTo(post.getContent());
    assertThat(apiResponse.getData().isFollowed()).isEqualTo(false);

  }


  @Test
  void getPostDetails_존재하지_않는_게시물() {
    // given
    Long postId = RandomValue.getRandomLong(-999,-1);

    String url = "http://localhost:" + port + "/api/v1/sns/getPostDetails/" + postId;

    NotFoundPostException exception = new NotFoundPostException();

    // when
    ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);

    ApiResponse<GetPostDetailsDto.Response> apiResponse = gson.fromJson(
            responseEntity.getBody(),
            new TypeToken<ApiResponse<GetPostDetailsDto.Response>>() {}.getType()
    );

    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertNull(apiResponse.getData());
    assertThat(apiResponse.getCode()).isEqualTo(exception.getCode());
    assertThat(apiResponse.getMessage()).isEqualTo(exception.getMessage());

  }



  // note. PostType 랜덤 지정하도록 변경 필요
  @Test
  void createPost_성공() {
    // given
    Member member = createMember();
    TokenDto tokenDto = tokenProvider.getToken(member);

    List<Long> nullList = new ArrayList<>();
    CreatePostDto.Request request = new CreatePostDto.Request(
        PostType.MEMBER,
        RandomValue.string(100).setNullable(false).get(),
        RandomValue.string(2000).setNullable(false).get(),
        nullList,
        nullList
    );

    String url = "http://localhost:" + port + "/api/v1/sns/createPost";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(tokenDto.getAccessToken());
    HttpEntity<CreatePostDto.Request> requestEntity = new HttpEntity<>(request, headers);

    // when
    ResponseEntity<String> responseEntity = restTemplate.postForEntity(
        url,
        requestEntity,
        String.class
    );

    ApiResponse<CreatePostDto.Response> apiResponse = gson.fromJson(
        responseEntity.getBody(),
        new TypeToken<ApiResponse<CreatePostDto.Response>>() {}.getType()
    );

    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertNotNull(apiResponse.getData());
    assertThat(apiResponse.getData().postType()).isEqualTo(request.postType());
    assertThat(apiResponse.getData().title()).isEqualTo(request.title());
    assertThat(apiResponse.getData().content()).isEqualTo(request.content());

  }


  @Test
  void createPost_공개_비공개_중복_입력() {
    // given
    Member member = createMember();
    TokenDto tokenDto = tokenProvider.getToken(member);

    List<Long> memberIds = List.of(1L, 2L);
    CreatePostDto.Request request = new CreatePostDto.Request(
        PostType.MEMBER,
        RandomValue.string(100).setNullable(false).get(),
        RandomValue.string(2000).setNullable(false).get(),
        memberIds,
        memberIds
    );

    String url = "http://localhost:" + port + "/api/v1/sns/createPost";

    InvalidMemberVisibilityException exception = new InvalidMemberVisibilityException();

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(tokenDto.getAccessToken());
    HttpEntity<CreatePostDto.Request> requestEntity = new HttpEntity<>(request, headers);

    // when
    ResponseEntity<String> responseEntity = restTemplate.postForEntity(
        url,
        requestEntity,
        String.class
    );

    ApiResponse<CreatePostDto.Response> apiResponse = gson.fromJson(
        responseEntity.getBody(),
        new TypeToken<ApiResponse<CreatePostDto.Response>>() {}.getType()
    );

    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(apiResponse.getCode()).isEqualTo(exception.getCode());
    assertThat(apiResponse.getMessage()).isEqualTo(exception.getMessage());
    assertNull(apiResponse.getData());
  }

  @Test
  void createPost_토큰_미입력() {
    // given
    List<Long> memberIds = List.of(1L, 2L);
    CreatePostDto.Request request = new CreatePostDto.Request(
        PostType.MEMBER,
        RandomValue.string(100).setNullable(false).get(),
        RandomValue.string(2000).setNullable(false).get(),
        memberIds,
        memberIds
    );

    String url = "http://localhost:" + port + "/api/v1/sns/createPost";

    UnAuthenticationMemberException exception = new UnAuthenticationMemberException();


    // when
    ResponseEntity<String> responseEntity = restTemplate.postForEntity(
        url,
        request,
        String.class
    );

    ApiResponse<CreatePostDto.Response> apiResponse = gson.fromJson(
        responseEntity.getBody(),
        new TypeToken<ApiResponse<CreatePostDto.Response>>() {}.getType()
    );

    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(apiResponse.getCode()).isEqualTo(exception.getCode());
    assertThat(apiResponse.getMessage()).isEqualTo(exception.getMessage());
    assertNull(apiResponse.getData());
  }


  @Test
  void uploadPostMedia() {
    // given
    String url = "http://localhost:" + port + "/api/v1/sns/uploadPostMedia";
    String returnUrl = "http://" + RandomValue.string(10,50).setNullable(false).setLanguages(Language.ENGLISH).get();

    MockMultipartFile mockFile = new MockMultipartFile(
            "file",
            "test-image.jpg",
            "image/jpeg",
            "test-image-content".getBytes()
    );

    // when
    when(s3Manager.upload(any(MultipartFile.class))).thenReturn(returnUrl);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", mockFile.getResource());
    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    ResponseEntity<String> responseEntity = restTemplate.postForEntity(
            url,
            requestEntity,
            String.class
    );

    ApiResponse<UploadPostMediaDto.Response> apiResponse = gson.fromJson(
            responseEntity.getBody(),
            new TypeToken<ApiResponse<UploadPostMediaDto.Response>>() {}.getType()
    );

    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(apiResponse.getData()).isNotNull();
    assertThat(apiResponse.getData().url()).isEqualTo(returnUrl);
  }


  @Test
  void createComment() {
    // given
    Member member = createMember();
    TokenDto tokenDto = tokenProvider.getToken(member);
    Member writeMember = createMember();
    PostType postType = RandomValue.getRandomEnum(PostType.class);
    Post post = createPost(postType, writeMember);


    CreateCommentDto.Request request = new CreateCommentDto.Request(
        post.getId(),
        RandomValue.string(255).setNullable(false).get()
    );

    String url = "http://localhost:" + port + "/api/v1/sns/createComment";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(tokenDto.getAccessToken());
    HttpEntity<CreateCommentDto.Request> requestEntity = new HttpEntity<>(request, headers);

    // when
    ResponseEntity<String> responseEntity = restTemplate.postForEntity(
        url,
        requestEntity,
        String.class
    );

    ApiResponse<CreateCommentDto.Response> apiResponse = gson.fromJson(
        responseEntity.getBody(),
        new TypeToken<ApiResponse<CreateCommentDto.Response>>() {}.getType()
    );
    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(apiResponse.getData()).isNotNull();
    assertThat(apiResponse.getData().postId()).isEqualTo(request.postId());
    assertThat(apiResponse.getData().content()).isEqualTo(request.content());
  }

  @Test
  void createComment_토큰_미입력() {
    // given
    Member writeMember = createMember();
    PostType postType = RandomValue.getRandomEnum(PostType.class);
    Post post = createPost(postType, writeMember);

    CreateCommentDto.Request request = new CreateCommentDto.Request(
        post.getId(),
        RandomValue.string(255).setNullable(false).get()
    );

    String url = "http://localhost:" + port + "/api/v1/sns/createComment";

    UnAuthenticationMemberException exception = new UnAuthenticationMemberException();

    // when
    ResponseEntity<String> responseEntity = restTemplate.postForEntity(
        url,
        request,
        String.class
    );

    ApiResponse<CreateCommentDto.Response> apiResponse = gson.fromJson(
        responseEntity.getBody(),
        new TypeToken<ApiResponse<CreateCommentDto.Response>>() {}.getType()
    );
    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(apiResponse.getCode()).isEqualTo(exception.getCode());
    assertThat(apiResponse.getMessage()).isEqualTo(exception.getMessage());
    assertNull(apiResponse.getData());
  }

  @Test
  void searchMember() {
    // given
    String nickname = RandomValue
        .string(1,50)
        .setNullable(false)
        .setLanguages(Language.ENGLISH)
        .get();

    Member searchMember = Member.builder()
        .nickname(nickname)
        .build();
    OAuthInfoDto oAuthInfo = OAuthInfoDto.builder()
        .snsId(RandomValue.string(255).setNullable(false).get())
        .email(RandomValue.string(255).setNullable(false).getEmail())
        .snsType(RandomValue.getRandomEnum(SnsType.class))
        .build();
    searchMember.addOAuthInfo(oAuthInfo);
    searchMember.updateProfile(RandomValue.string(255).get());
    memberRepository.save(searchMember);

    Member member = createMember();
    TokenDto tokenDto = tokenProvider.getToken(member);
    String url = "http://localhost:" + port + "/api/v1/sns/searchMember/"
        + nickname;


    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(tokenDto.getAccessToken());
    HttpEntity<String> requestEntity = new HttpEntity<>(headers);

    // when
    ResponseEntity<String> responseEntity = restTemplate.getForEntity(
        url,
        String.class,
        requestEntity
    );

    ApiResponse<List<SearchMemberDto.Response>> apiResponse = gson.fromJson(
        responseEntity.getBody(),
        new TypeToken<ApiResponse<List<SearchMemberDto.Response>>>() {}.getType()
    );
    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(apiResponse.getData()).isNotNull();
    assertThat(apiResponse.getData().get(0).memberId()).isEqualTo(searchMember.getId());
    assertThat(apiResponse.getData().get(0).nickname()).isEqualTo(searchMember.getNickname());
  }


//  @Test
//  void searchMember_토큰_없음() {
//    // given
//    String url = "http://localhost:" + port + "/api/v1/sns/searchMember/"
//        + "nickname";
//
//    UnAuthenticationMemberException exception = new UnAuthenticationMemberException();
//
//    // when
//    ResponseEntity<String> responseEntity = restTemplate.getForEntity(
//        url,
//        String.class
//    );
//
//    ApiResponse<List<SearchMemberDto.Response>> apiResponse = gson.fromJson(
//        responseEntity.getBody(),
//        new TypeToken<ApiResponse<List<SearchMemberDto.Response>>>() {}.getType()
//    );
//    // then
//    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
//    assertThat(apiResponse.getCode()).isEqualTo(exception.getCode());
//    assertThat(apiResponse.getMessage()).isEqualTo(exception.getMessage());
//    assertNull(apiResponse.getData());
//  }

  @Test
  void getLatestPosts_토큰_미보유() {
    // given
    int memberCount = RandomValue.getInt(0, 5);

    List<GetLatestPostsDto.Response> expectedList = new ArrayList<>();

    for (int i = 0; i < memberCount; i++) {
      Member writeMember = createMember();
      writeMember.updateRole(Role.STREAMER);

      String dailyMessage = (RandomValue.getInt(0,2) == 1) ? null : RandomValue.string(1, 50).setNullable(false).get();
      if (dailyMessage != null)  {
        dailyMessageRepository.save(writeMember.getId(), dailyMessage, 24);
      }

      int postCount = RandomValue.getInt(0, 5);
      List<Long> postList = IntStream.range(0, postCount)
              .mapToObj(a -> createPost(PostType.STREAMER, writeMember))
              .map(Post::getId)
              .sorted()
              .toList();

      if (!postList.isEmpty()) {
        expectedList.add(new GetLatestPostsDto.Response(
                writeMember.getId(),
                writeMember.getProfile(),
                dailyMessage,
                postList
        ));
      }
    }

    String url = "http://localhost:" + port + "/api/v1/sns/getLatestPosts";
    ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);

    ApiResponse<List<GetLatestPostsDto.Response>> apiResponse = gson.fromJson(
            responseEntity.getBody(),
            new TypeToken<ApiResponse<List<GetLatestPostsDto.Response>>>() {}.getType()
    );

    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(apiResponse.getData()).isNotNull();

    Comparator<GetLatestPostsDto.Response> getMemberId = Comparator.comparing(GetLatestPostsDto.Response::memberId);
    List<GetLatestPostsDto.Response> actualList = apiResponse.getData().stream()
            .map(dto -> new GetLatestPostsDto.Response(
                    dto.memberId(),
                    dto.profileUrl(),
                    dto.dailyMessage(),
                    dto.postIdList().stream().sorted().toList()
            ))
            .sorted(getMemberId)
            .toList();

    assertThat(actualList).usingRecursiveComparison().isEqualTo(expectedList);
  }


  @Test
  void getLatestPosts_토큰_보유() {
    // given
    Member member = createMember();
    TokenDto tokenDto = tokenProvider.getToken(member);

    Member writeMember = createMember();
    writeMember.updateRole(Role.STREAMER);

    List<GetLatestPostsDto.Response> expectedList = new ArrayList<>();
    int readPostCount = RandomValue.getInt(0, 5);
    int newPostCount = RandomValue.getInt(0, 5);

    IntStream.range(0, readPostCount)
            .forEach(a -> {
              Post post = createPost(PostType.STREAMER, writeMember);
              readerPostRepository.addReadPost(member.getId().toString(), post.getId().toString());
            });

    List<Long> postList = IntStream.range(0, newPostCount)
            .mapToObj(a -> createPost(PostType.STREAMER, writeMember))
            .map(Post::getId)
            .sorted()
            .toList();


    String dailyMessage = dailyMessageRepository.getDailyMessage(writeMember.getId());

    if (!postList.isEmpty()) {
      expectedList.add(new GetLatestPostsDto.Response(
              writeMember.getId(),
              writeMember.getProfile(),
              dailyMessage,
              postList
      ));
    }


    String url = "http://localhost:" + port + "/api/v1/sns/getLatestPosts";


    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(tokenDto.getAccessToken());
    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

    // when
    ResponseEntity<String> responseEntity = restTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            String.class
    );


    ApiResponse<List<GetLatestPostsDto.Response>> apiResponse = gson.fromJson(
            responseEntity.getBody(),
            new TypeToken<ApiResponse<List<GetLatestPostsDto.Response>>>() {}.getType()
    );

    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(apiResponse.getData()).isNotNull();

    Comparator<GetLatestPostsDto.Response> getMemberId = Comparator.comparing(GetLatestPostsDto.Response::memberId);
    List<GetLatestPostsDto.Response> actualList = apiResponse.getData().stream()
            .map(dto -> new GetLatestPostsDto.Response(
                    dto.memberId(),
                    dto.profileUrl(),
                    dto.dailyMessage(),
                    dto.postIdList().stream().sorted().toList()
            ))
            .sorted(getMemberId)
            .toList();

    assertThat(actualList).usingRecursiveComparison().isEqualTo(expectedList);
  }


  @Test
  void reportPost() {
    // given
    Member member = createMember();
    TokenDto tokenDto = tokenProvider.getToken(member);
    Member writeMember = createMember();
    PostType postType = RandomValue.getRandomEnum(PostType.class);
    Post post = createPost(postType, writeMember);


    ReportPostDto.Request request = new ReportPostDto.Request(post.getId());

    String url = "http://localhost:" + port + "/api/v1/sns/reportPost";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(tokenDto.getAccessToken());
    HttpEntity<ReportPostDto.Request> requestEntity = new HttpEntity<>(request, headers);

    // when
    ResponseEntity<String> responseEntity = restTemplate.postForEntity(
            url,
            requestEntity,
            String.class
    );

    ApiResponse<CreateCommentDto.Response> apiResponse = gson.fromJson(
            responseEntity.getBody(),
            new TypeToken<ApiResponse<CreateCommentDto.Response>>() {}.getType()
    );
    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(apiResponse.getData()).isNull();
  }

  @Test
  void reportPost_중복_신고() {
    // given
    Member member = createMember();
    TokenDto tokenDto = tokenProvider.getToken(member);
    Member writeMember = createMember();
    PostType postType = RandomValue.getRandomEnum(PostType.class);
    Post post = createPost(postType, writeMember);
    createReportPost(post, member);


    ReportPostDto.Request request = new ReportPostDto.Request(post.getId());

    String url = "http://localhost:" + port + "/api/v1/sns/reportPost";

    DuplicateReportException exception = new DuplicateReportException();

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(tokenDto.getAccessToken());
    HttpEntity<ReportPostDto.Request> requestEntity = new HttpEntity<>(request, headers);

    // when
    ResponseEntity<String> responseEntity = restTemplate.postForEntity(
            url,
            requestEntity,
            String.class
    );

    ApiResponse<CreateCommentDto.Response> apiResponse = gson.fromJson(
            responseEntity.getBody(),
            new TypeToken<ApiResponse<CreateCommentDto.Response>>() {}.getType()
    );
    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(apiResponse.getCode()).isEqualTo(exception.getCode());
    assertThat(apiResponse.getMessage()).isEqualTo(exception.getMessage());
    assertThat(apiResponse.getData()).isNull();
  }

  @Test
  void reportPost_토큰_미입력() {
    // given
    Member writeMember = createMember();
    PostType postType = RandomValue.getRandomEnum(PostType.class);
    Post post = createPost(postType, writeMember);

    ReportPostDto.Request request = new ReportPostDto.Request(post.getId());

    String url = "http://localhost:" + port + "/api/v1/sns/reportPost";

    UnAuthenticationMemberException exception = new UnAuthenticationMemberException();

    // when
    ResponseEntity<String> responseEntity = restTemplate.postForEntity(
            url,
            request,
            String.class
    );

    ApiResponse<CreateCommentDto.Response> apiResponse = gson.fromJson(
            responseEntity.getBody(),
            new TypeToken<ApiResponse<CreateCommentDto.Response>>() {}.getType()
    );
    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(apiResponse.getCode()).isEqualTo(exception.getCode());
    assertThat(apiResponse.getMessage()).isEqualTo(exception.getMessage());
    assertNull(apiResponse.getData());
  }

  @Test
  void reportPost_존재하지_않는_게시물() {
    // given
    TokenDto tokenDto = tokenProvider.getToken(createMember());

    Long postId = RandomValue.getRandomLong(-9999, -1);
    ReportPostDto.Request request = new ReportPostDto.Request(postId);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(tokenDto.getAccessToken());
    HttpEntity<ReportPostDto.Request> requestEntity = new HttpEntity<>(request, headers);

    String url = "http://localhost:" + port + "/api/v1/sns/reportPost";

    // when
    ResponseEntity<String> responseEntity = restTemplate.postForEntity(
            url,
            requestEntity,
            String.class
    );

    ApiResponse<CreateCommentDto.Response> apiResponse = gson.fromJson(
            responseEntity.getBody(),
            new TypeToken<ApiResponse<CreateCommentDto.Response>>() {}.getType()
    );

    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertNull(apiResponse.getData());
  }

  @Test
  void reportComment() {
    // given
    Member member = createMember();
    TokenDto tokenDto = tokenProvider.getToken(member);

    Member writeMember = createMember();
    PostType postType = RandomValue.getRandomEnum(PostType.class);
    Post post = createPost(postType, writeMember);

    Comment comment = createComment(post, writeMember);

    ReportCommentDto.Request request = new ReportCommentDto.Request(comment.getId());

    String url = "http://localhost:" + port + "/api/v1/sns/reportComment";

    HttpEntity<ReportCommentDto.Request> requestEntity = new HttpEntity<>(request,
            new HttpHeaders() {{
              setBearerAuth(tokenDto.getAccessToken());
            }}
    );


    // when
    ResponseEntity<String> responseEntity = restTemplate.postForEntity(
            url,
            requestEntity,
            String.class
    );

    ApiResponse<CreateCommentDto.Response> apiResponse = gson.fromJson(
            responseEntity.getBody(),
            new TypeToken<ApiResponse<CreateCommentDto.Response>>() {}.getType()
    );
    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(apiResponse.getData()).isNull();
  }

  @Test
  void reportComment_신고_처리_중_중복_신고() {
    // given
    Member member = createMember();
    TokenDto tokenDto = tokenProvider.getToken(member);

    Member writeMember = createMember();
    PostType postType = RandomValue.getRandomEnum(PostType.class);
    Post post = createPost(postType, writeMember);

    Comment comment = createComment(post, writeMember);
    createReportComment(comment, member);


    ReportCommentDto.Request request = new ReportCommentDto.Request(comment.getId());

    String url = "http://localhost:" + port + "/api/v1/sns/reportComment";

    DuplicateReportException exception = new DuplicateReportException();

    HttpEntity<ReportCommentDto.Request> requestEntity = new HttpEntity<>(request,
            new HttpHeaders() {{
              setBearerAuth(tokenDto.getAccessToken());
            }}
    );


    // when
    ResponseEntity<String> responseEntity = restTemplate.postForEntity(
            url,
            requestEntity,
            String.class
    );

    ApiResponse<CreateCommentDto.Response> apiResponse = gson.fromJson(
            responseEntity.getBody(),
            new TypeToken<ApiResponse<CreateCommentDto.Response>>() {}.getType()
    );
    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(apiResponse.getCode()).isEqualTo(exception.getCode());
    assertThat(apiResponse.getMessage()).isEqualTo(exception.getMessage());
    assertThat(apiResponse.getData()).isNull();
  }

  @Test
  void reportComment_신고_처리_후_중복_신고() throws NoSuchFieldException, IllegalAccessException {
    // given
    Member member = createMember();
    TokenDto tokenDto = tokenProvider.getToken(member);

    Member writeMember = createMember();
    PostType postType = RandomValue.getRandomEnum(PostType.class);
    Post post = createPost(postType, writeMember);

    Comment comment = createComment(post, writeMember);
    ReportComment reportComment = createReportComment(comment, member);

    int randomInt = RandomValue.getInt(0,2);
    ReportStatus status = (randomInt == 0) ? ReportStatus.CANCEL : ReportStatus.DELETED;

    Field contentField = ReportComment.class.getDeclaredField("status");
    contentField.setAccessible(true);
    contentField.set(reportComment, status);

    System.out.println("status : " + reportComment.getStatus());

    ReportCommentDto.Request request = new ReportCommentDto.Request(comment.getId());

    String url = "http://localhost:" + port + "/api/v1/sns/reportComment";

    DuplicateReportException exception = new DuplicateReportException();

    HttpEntity<ReportCommentDto.Request> requestEntity = new HttpEntity<>(request,
            new HttpHeaders() {{
              setBearerAuth(tokenDto.getAccessToken());
            }}
    );


    // when
    ResponseEntity<String> responseEntity = restTemplate.postForEntity(
            url,
            requestEntity,
            String.class
    );

    ApiResponse<CreateCommentDto.Response> apiResponse = gson.fromJson(
            responseEntity.getBody(),
            new TypeToken<ApiResponse<CreateCommentDto.Response>>() {}.getType()
    );
    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(apiResponse.getCode()).isEqualTo(exception.getCode());
    assertThat(apiResponse.getMessage()).isEqualTo(exception.getMessage());
    assertThat(apiResponse.getData()).isNull();
  }

  @Test
  void reportComment_토큰_미입력() {
    // given
    Long randomLong = RandomValue.getRandomLong(0, 9999);
    ReportCommentDto.Request request = new ReportCommentDto.Request(randomLong);

    String url = "http://localhost:" + port + "/api/v1/sns/reportComment";

    UnAuthenticationMemberException exception = new UnAuthenticationMemberException();

    // when
    ResponseEntity<String> responseEntity = restTemplate.postForEntity(
            url,
            request,
            String.class
    );

    ApiResponse<CreateCommentDto.Response> apiResponse = gson.fromJson(
            responseEntity.getBody(),
            new TypeToken<ApiResponse<CreateCommentDto.Response>>() {}.getType()
    );
    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(apiResponse.getCode()).isEqualTo(exception.getCode());
    assertThat(apiResponse.getMessage()).isEqualTo(exception.getMessage());
    assertThat(apiResponse.getData()).isNull();
  }


  @Test
  void getPostDetails_후_getLatestPosts() {
    // given
    int postCount = 0;

    Member member = createMember();
    TokenDto tokenDto = tokenProvider.getToken(member);

    Member writeMember = createMember();
    PostType postType = RandomValue.getRandomEnum(PostType.class);
    Post post = createPost(postType, writeMember);

    String getPostDetailsUrl = "http://localhost:" + port + "/api/v1/sns/getPostDetails/" + post.getId();
    String getLatestPostsUrl = "http://localhost:" + port + "/api/v1/sns/getLatestPosts";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(tokenDto.getAccessToken());
    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

    // when
    ResponseEntity<String> getPostDetailsResponse = restTemplate.exchange(
            getPostDetailsUrl,
            HttpMethod.GET,
            requestEntity,
            String.class);

    ApiResponse<GetPostDetailsDto.Response> postDetailsApiResponse = gson.fromJson(
            getPostDetailsResponse.getBody(),
            new TypeToken<ApiResponse<GetPostDetailsDto.Response>>() {}.getType()
    );

    ResponseEntity<String> getLatestPostsResponse = restTemplate.exchange(
            getLatestPostsUrl,
            HttpMethod.GET,
            requestEntity,
            String.class
    );

    ApiResponse<List<GetLatestPostsDto.Response>> latestPostsApiResponse = gson.fromJson(
            getLatestPostsResponse.getBody(),
            new TypeToken<ApiResponse<List<GetLatestPostsDto.Response>>>() {}.getType()
    );

    // then
    assertThat(getPostDetailsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertNotNull(postDetailsApiResponse.getData());
    assertThat(postDetailsApiResponse.getData().postId()).isEqualTo(post.getId());
    assertThat(postDetailsApiResponse.getData().writerId()).isEqualTo(writeMember.getId());
    assertThat(postDetailsApiResponse.getData().title()).isEqualTo(post.getTitle());
    assertThat(postDetailsApiResponse.getData().nickname()).isEqualTo(writeMember.getNickname());
    assertThat(postDetailsApiResponse.getData().profileUrl()).isEqualTo(writeMember.getProfile());
    assertThat(postDetailsApiResponse.getData().content()).isEqualTo(post.getContent());

    assertThat(latestPostsApiResponse.getData().size()).isEqualTo(postCount);

  }
}