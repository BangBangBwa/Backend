package com.bangbangbwa.backend.domain.sns.controller;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static com.mongodb.assertions.Assertions.assertNull;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.bangbangbwa.backend.domain.member.common.entity.Member;
import com.bangbangbwa.backend.domain.member.common.enums.Role;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.bangbangbwa.backend.domain.member.exception.UnAuthenticationMemberException;
import com.bangbangbwa.backend.domain.member.repository.MemberRepository;
import com.bangbangbwa.backend.domain.oauth.common.dto.OAuthInfoDto;
import com.bangbangbwa.backend.domain.oauth.common.enums.SnsType;
import com.bangbangbwa.backend.domain.sns.common.dto.CreateCommentDto;
import com.bangbangbwa.backend.domain.sns.common.dto.CreatePostDto;
import com.bangbangbwa.backend.domain.sns.common.dto.GetLatestPostsDto;
import com.bangbangbwa.backend.domain.sns.common.dto.GetPostDetailsDto;
import com.bangbangbwa.backend.domain.sns.common.dto.GetPostListDto;
import com.bangbangbwa.backend.domain.sns.common.dto.SearchMemberDto;
import com.bangbangbwa.backend.domain.sns.common.dto.UploadPostMediaDto;
import com.bangbangbwa.backend.domain.sns.common.entity.Post;
import com.bangbangbwa.backend.domain.sns.common.enums.PostType;
import com.bangbangbwa.backend.domain.sns.exception.InvalidMemberVisibilityException;
import com.bangbangbwa.backend.domain.sns.repository.CommentRepository;
import com.bangbangbwa.backend.domain.sns.repository.PostRepository;
import com.bangbangbwa.backend.domain.token.business.TokenProvider;
import com.bangbangbwa.backend.domain.token.common.TokenDto;
import com.bangbangbwa.backend.domain.token.common.entity.Token;
import com.bangbangbwa.backend.global.response.ApiResponse;
import com.bangbangbwa.backend.global.test.IntegrationTest;
import com.bangbangbwa.backend.global.util.S3Manager;
import com.bangbangbwa.backend.global.util.randomValue.Language;
import com.bangbangbwa.backend.global.util.randomValue.RandomValue;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
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

  @RepeatedTest(5)
  void getPostList_성공() {
    // given
    PostType postType = PostType.STREAMER;


    List<Post> postList = postRepository.findAllByPostType(postType);
    System.out.println(postList.size());
    System.out.println(postList.size());
    System.out.println(postList.size());
    System.out.println(postList.size());
    System.out.println(postList.size());


    Member writeMember = createMember();
    int postCount = 3;

    for(int i=0; i<postCount; i++) {
      createPost(postType, writeMember);
    }
//    List<Post> expectedPosts = IntStream.range(0, postCount)
//        .mapToObj(i -> createPost(postType, writeMember))
//        .toList();



    postList = postRepository.findAllByPostType(postType);
    System.out.println(postList.size());
    System.out.println(postList.size());
    System.out.println(postList.size());
    System.out.println(postList.size());
    System.out.println(postList.size());

    String url = "http://localhost:" + port + "/api/v1/sns/getPostList";


    // when
    ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);

    ApiResponse<List<GetPostListDto.Response>> apiResponse = gson.fromJson(
        responseEntity.getBody(),
        new TypeToken<ApiResponse<List<GetPostListDto.Response>>>() {}.getType()
    );

    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertNotNull(apiResponse.getData());
    assertThat(apiResponse.getData().size()).isEqualTo(postCount);

//    List<GetPostListDto.Response> actualPosts = apiResponse.getData();
//    for (int i = 0; i < postCount; i++) {
//      Post expectedPost = expectedPosts.get(i);
//      GetPostListDto.Response actualPost = actualPosts.get(i);
//
//      assertThat(actualPost.postId()).isEqualTo(expectedPost.getId());
//      assertThat(actualPost.title()).isEqualTo(expectedPost.getTitle());;
//    }
  }


  @Test
  void getPostDetails() {
    // given
    Member writeMember = createMember();
    PostType postType = RandomValue.getRandomEnum(PostType.class);
    Post post = createPost(postType, writeMember);

    String url = "http://localhost:" + port + "/api/v1/sns/getPostDetails/" + post.getId();

    // when
    ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);

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
//    assertThat(apiResponse.getData().comment()).isEqualTo();

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


//  @Test
//  void uploadPostMedia() throws IOException {
//    // given
//    MultipartFile mockFile = mock(MultipartFile.class);
//
//    String url = "http://localhost:" + port + "/api/v1/sns/uploadPostMedia";
//
//    // when
//    when(s3Manager.upload(any(MultipartFile.class))).thenReturn("urldkadjfkasjfk");
//
//    HttpHeaders headers = new HttpHeaders();
//    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//    HttpEntity<MultipartFile> requestEntity = new HttpEntity<>(mockFile, headers);
//
//
//
//    ResponseEntity<String> responseEntity = restTemplate.postForEntity(
//        url,
//        requestEntity,
//        String.class
//    );
//
//    ApiResponse<UploadPostMediaDto.Response> apiResponse = gson.fromJson(
//        responseEntity.getBody(),
//        new TypeToken<ApiResponse<UploadPostMediaDto.Response>>() {}.getType()
//    );


    // then
//  }

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
        .string(50)
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
    HttpEntity<String> requestEntity = new HttpEntity<>(headers); // headers만 포함

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

  // note. 스트리머 관련 작업 필요
  @Test
  void getLatestPosts() {
    // given
    Member member = createMember();
    Post post = createPost(PostType.STREAMER, member);

    String url = "http://localhost:" + port + "/api/v1/sns/getLatestPosts";

    // when
    ResponseEntity<String> responseEntity = restTemplate.getForEntity(
        url,
        String.class
    );

    ApiResponse<List<GetLatestPostsDto.Response>> apiResponse = gson.fromJson(
        responseEntity.getBody(),
        new TypeToken<ApiResponse<List<GetLatestPostsDto.Response>>>() {}.getType()
    );

    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(apiResponse.getData()).isNotNull();
  }
}