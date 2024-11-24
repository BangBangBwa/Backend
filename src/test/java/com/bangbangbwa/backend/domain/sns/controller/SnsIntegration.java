//package com.bangbangbwa.backend.domain.sns.controller;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import com.bangbangbwa.backend.domain.member.common.entity.Member;
//import com.bangbangbwa.backend.domain.member.common.enums.Role;
//import com.bangbangbwa.backend.domain.member.repository.MemberRepository;
//import com.bangbangbwa.backend.domain.oauth.common.dto.OAuthInfoDto;
//import com.bangbangbwa.backend.domain.oauth.common.enums.SnsType;
//import com.bangbangbwa.backend.domain.sns.common.dto.CreatePostDto;
//import com.bangbangbwa.backend.domain.sns.common.dto.GetPostDetailsDto;
//import com.bangbangbwa.backend.domain.sns.common.dto.GetPostListDto;
//import com.bangbangbwa.backend.domain.sns.common.entity.Post;
//import com.bangbangbwa.backend.domain.sns.common.enums.PostType;
//import com.bangbangbwa.backend.domain.sns.repository.CommentRepository;
//import com.bangbangbwa.backend.domain.sns.repository.PostRepository;
//import com.bangbangbwa.backend.domain.token.business.TokenProvider;
//import com.bangbangbwa.backend.domain.token.common.TokenDto;
//import com.bangbangbwa.backend.global.test.IntegrationTest;
//import com.bangbangbwa.backend.global.util.randomValue.RandomValue;
//import com.bangbangbwa.backend.global.util.randomValue.RandomValueGenerator;
//import java.util.ArrayList;
//import java.util.List;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.transaction.annotation.Transactional;
//
//@Transactional
//class SnsIntegrationTest extends IntegrationTest {
//
//  @Autowired
//  private MemberRepository memberRepository;
//
//  @Autowired
//  private PostRepository postRepository;
//
//  @Autowired
//  private TokenProvider tokenProvider;
//
//  @Autowired
//  private CommentRepository commentRepository;
//
//  private Member getMember() {
//    Member member = Member.builder()
//        .nickname(RandomValue.string(255).setNullable(false).get())
//        .build();
//    OAuthInfoDto oAuthInfo = OAuthInfoDto.builder()
//        .snsId(RandomValue.string(255).setNullable(false).get())
//        .email(RandomValue.string(255).setNullable(false).getEmail())
//        .snsType(RandomValue.getRandomEnum(SnsType.class))
//        .build();
//    member.addOAuthInfo(oAuthInfo);
//    member.updateProfile(RandomValue.string(255).get());
//
//    return member;
//  }
//
//  private Member createMember() {
//    Member member = getMember();
//    memberRepository.save(member);
//    return member;
//  }
//
//  private Post getPost(PostType postType, Member member) {
//    return Post.builder()
//        .postType(postType)
//        .memberId(member.getId())
//        .title(RandomValue.string(100).setNullable(false).get())
//        .content(RandomValue.string(2000).setNullable(false).get())
//        .build();
//  }
//
//  private Post createPost(PostType postType, Member writeMember) {
//    Post post = getPost(postType, writeMember);
//    postRepository.save(post);
//    return post;
//  }
//
//  @Test
//  void getPostList_게시물_없을_경우() throws Exception {
//    Member member = createMember();
//    TokenDto token = tokenProvider.getToken(member);
//
//    String URL = "/api/v1/sns/getPostList";
//
//    mvc.perform(get(URL)
//            .header(HttpHeaders.AUTHORIZATION, token.getAccessToken()))
//        .andExpect(status().isOk())
//        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//        .andExpect(jsonPath("$.code").value(HttpStatus.OK.name()))
//        .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
//        .andExpect(jsonPath("$.data").isEmpty())
//    ;
//  }
//
//  @Test
//  void getPostList_게시물_존재() throws Exception {
//    Member member = createMember();
//    TokenDto token = tokenProvider.getToken(member);
//    Member writeMember = createMember();
//    Post post = createPost(PostType.STREAMER, writeMember);
//
//
//
//    String URL = "/api/v1/sns/getPostList";
//
//    GetPostListDto.Response expected = new GetPostListDto.Response(
//        post.getId(),
//        post.getTitle(),
//        false,
//        false
//        );
//
//    mvc.perform(get(URL)
//            .header(HttpHeaders.AUTHORIZATION, token.getAccessToken()))
//        .andExpect(status().isOk())
//        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//        .andExpect(jsonPath("$.code").value(HttpStatus.OK.name()))
//        .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
//        .andExpect(jsonPath("$.data").isNotEmpty())
//        .andExpect(jsonPath("$.data[0].postId").value(expected.postId()))
//        .andExpect(jsonPath("$.data[0].title").value(expected.title()))
//        .andExpect(jsonPath("$.data[0].hasImage").value(expected.hasImage()))
//        .andExpect(jsonPath("$.data[0].hasVideo").value(expected.hasVideo()))
//    ;
//  }
//  @Test
//  void getPostDetails() throws Exception {
////    Member member = createMember();
////    TokenDto token = tokenProvider.getToken(member);
////    System.out.println(token.getAccessToken());
////    System.out.println(token.getRefreshToken());
////    System.out.println();
////    System.out.println();
////    System.out.println();
////
////    Member writeMember = createMember();
////    Post post = createPost(
////        RandomValue.getRandomEnum(PostType.class),
////        writeMember
////    );
////
////    String URL = "/api/v1/sns/getPostDetails/" + post.getId();
////    GetPostDetailsDto.Response expected = new GetPostDetailsDto.Response(
////        post.getId(),
////        writeMember.getId(),
////        post.getTitle(),
////        writeMember.getNickname(),
////        writeMember.getProfile(),
////        post.getContent(),
////        "a"
////    );
////
////    mvc.perform(get(URL)
////            .header(HttpHeaders.AUTHORIZATION, token.getAccessToken()))
////        .andExpect(status().isOk())
////        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
////        .andExpect(jsonPath("$.code").value(HttpStatus.OK.name()))
////        .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
////        .andExpect(jsonPath("$.data").isNotEmpty())
////        .andExpect(jsonPath("$.data.postId").value(expected.postId()))
////        .andExpect(jsonPath("$.data.title").value(expected.title()))
////        .andExpect(jsonPath("$.data.nickname").value(expected.nickname()))
////        .andExpect(jsonPath("$.data.profileUrl").value(expected.profileUrl()))
////        .andExpect(jsonPath("$.data.content").value(expected.content()))
//////        .andExpect(jsonPath("$.data.comment").value(expected.comment()))
//    ;
//  }
//
//  @Test
//  void createPost() throws Exception {
////    Member member = createMember();
////    TokenDto token = tokenProvider.getToken(member);
////    PostType postType = PostType.MEMBER;
////    Post post = createPost(postType, member);
////
////    String URL = "/api/v1/sns/createPost";
////
//////    List<Long> memberIds = new ArrayList<>();
////    List<Long> memberIds = List.of(1L);
////
////    CreatePostDto.Request request = new CreatePostDto.Request(
////        postType,
////        post.getTitle(),
////        post.getContent(),
////        memberIds,
////        memberIds
////    );
////
////    CreatePostDto.Response expected = new CreatePostDto.Response(
////        postType,
////        post.getTitle(),
////        post.getContent()
////    );
////
////    mvc.perform(post(URL)
////            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getAccessToken())
////            .contentType(MediaType.APPLICATION_JSON)
////            .content(objectMapper.writeValueAsBytes(request))
////        )
////        .andExpect(status().isOk())
////        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
////        .andExpect(jsonPath("$.code").value(HttpStatus.OK.name()))
////        .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
////        .andExpect(jsonPath("$.data").isNotEmpty())
////        .andExpect(jsonPath("$.data.postType").value(expected.postType()))
////        .andExpect(jsonPath("$.data.title").value(expected.title()))
////        .andExpect(jsonPath("$.data.content").value(expected.content()))
////    ;
//  }
//
//  @Test
//  void uploadPostMedia() {
//  }
//
//  @Test
//  void createComment() {
//  }
//
//  @Test
//  void searchMember() {
//  }
//
//  @Test
//  void getLatestPosts() {
//  }
//}