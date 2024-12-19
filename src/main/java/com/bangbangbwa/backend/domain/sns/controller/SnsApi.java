package com.bangbangbwa.backend.domain.sns.controller;

import com.bangbangbwa.backend.domain.post.common.dto.CreatePostDto;
import com.bangbangbwa.backend.domain.post.common.dto.GetPostDetailsDto;
import com.bangbangbwa.backend.domain.post.common.dto.GetPostListDto;
import com.bangbangbwa.backend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.MediaType;

@Tag(name = "SnsAPI", description = "SNS API")
public interface SnsApi {

//  @Operation(
//      tags = {"SnsAPI"},
//      summary = "팔로우한 사용자 최신글 조회",
//      description = "팔로우한 사용자의 최신글 목록을 조회합니다.",
//      responses = {
//          @io.swagger.v3.oas.annotations.responses.ApiResponse(
//              responseCode = "200",
//              description = "OK",
//              content = @Content(
//                  mediaType = MediaType.APPLICATION_JSON_VALUE,
//                  schema = @Schema(implementation = GetFollowedLatestPostsDto.Response.class)
//              )
//          )
//      }
//  )
//  public ApiResponse<Response> getFollowedLatestPosts();

//  @Operation(
//      tags = {"SnsAPI"},
//      summary = "게시글 목록 조회",
//      description = "게시글 목록을 조회합니다.",
//      responses = {
//          @io.swagger.v3.oas.annotations.responses.ApiResponse(
//              responseCode = "200",
//              description = "OK",
//              content = @Content(
//                  mediaType = MediaType.APPLICATION_JSON_VALUE,
//                  schema = @Schema(implementation = GetPostListDto.Response.class)
//              )
//          )
//      }
//  )
//  public ApiResponse<List<GetPostListDto.Response>> getPostList();

  @Operation(
      tags = {"SnsAPI"},
      summary = "게시글 상세 조회",
      description = "게시글 상세 정보를 조회합니다.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "200",
              description = "OK",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = GetPostDetailsDto.Response.class)
              )
          )
      }
  )
  public ApiResponse<GetPostDetailsDto.Response> getPostDetails(@Parameter Long postId);

//
//  @Operation(
//      tags = {"SnsAPI"},
//      summary = "게시글 작성",
//      description = "게시글을 작성합니다."
//  )
//  @ApiResponses(value = {
//      @io.swagger.v3.oas.annotations.responses.ApiResponse(
//          responseCode = "200",
//          description = "OK",
//          content = @Content(
//              mediaType = MediaType.APPLICATION_JSON_VALUE,
//              schema = @Schema(implementation = CreatePostDto.Response.class)
//          )
//      ),
//      @io.swagger.v3.oas.annotations.responses.ApiResponse(
//          responseCode = "401",
//          description = "권한 없음",
//          content = @Content(
//              mediaType = "application/json",
//              schema = @Schema(implementation = ApiResponse.class),
//              examples = @ExampleObject(
//                  value = """
//                      {
//                      "code": "NO_POST_PERMISSION",
//                      "message": "작성 권한이 없습니다.",
//                      "data" : null
//                      }
//                      """
//              )
//          )
//      ),
//      @io.swagger.v3.oas.annotations.responses.ApiResponse(
//          responseCode = "400",
//          description = "잘못된 요청",
//          content = @Content(
//              mediaType = "application/json",
//              schema = @Schema(implementation = ApiResponse.class),
//              examples = @ExampleObject(
//                  value = """
//                      {
//                      "code": "BAD_REQUEST",
//                      "message": "잘못된 요청입니다.",
//                      "data" : [
//                        "postType: 게시글 타입을 입력해주세요.",
//                        "title: 제목을 입력해주세요.",
//                        "content: 내용을 입력해주세요."
//                      ]
//                      }
//                      """
//              )
//          )
//      )
//  })
//  ApiResponse<CreatePostDto.Response> createPost(
//      @RequestBody CreatePostDto.Request request
//  );

}
