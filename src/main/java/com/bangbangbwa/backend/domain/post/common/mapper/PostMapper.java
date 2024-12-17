package com.bangbangbwa.backend.domain.post.common.mapper;

import com.bangbangbwa.backend.domain.member.common.dto.PostDto;
import com.bangbangbwa.backend.domain.post.common.dto.CreatePostDto;
import com.bangbangbwa.backend.domain.post.common.dto.GetPostListDto;
import com.bangbangbwa.backend.domain.post.common.dto.UploadPostMediaDto;
import com.bangbangbwa.backend.domain.post.common.entity.Post;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PostMapper {

  PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

  @Mapping(target = "postType", source = "request.postType")
  @Mapping(target = "title", source = "request.title")
  @Mapping(target = "content", source = "request.content")
  @Mapping(target = "publicMembers", source = "request.publicMembers")
  @Mapping(target = "privateMembers", source = "request.privateMembers")
  Post dtoToEntity(CreatePostDto.Request request);


  @Mapping(target = "postType", source = "post.postType")
  @Mapping(target = "title", source = "post.title")
  @Mapping(target = "content", source = "post.content")
  CreatePostDto.Response dtoToCreatePostResponse(Post post);

  @Mapping(target = "url", source = "url")
  UploadPostMediaDto.Response dtoToUploadPostMediaResponse(String url);

  @Mapping(target = "postId", source = "id")
  GetPostListDto.Response dtoToGetPostListResponse(Post post);

  List<GetPostListDto.Response> dtoToGetPostListResponse(List<Post> postList);


  List<PostDto.PostResponse> dtoToPostResponse(List<PostDto> postDtos);

  default PostDto.Response dtoToResponse(List<PostDto> postDtos) {
    List<PostDto.PostResponse> postResponses = dtoToPostResponse(postDtos);
    return new PostDto.Response(postResponses);
  }
}