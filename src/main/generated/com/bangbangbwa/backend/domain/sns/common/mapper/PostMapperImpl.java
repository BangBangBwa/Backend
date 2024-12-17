package com.bangbangbwa.backend.domain.sns.common.mapper;

import com.bangbangbwa.backend.domain.member.common.dto.PostDto;
import com.bangbangbwa.backend.domain.sns.common.dto.CreatePostDto;
import com.bangbangbwa.backend.domain.sns.common.dto.GetPostListDto;
import com.bangbangbwa.backend.domain.sns.common.dto.UploadPostMediaDto;
import com.bangbangbwa.backend.domain.sns.common.entity.Post;
import com.bangbangbwa.backend.domain.sns.common.enums.PostType;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-16T16:40:29+0900",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.13 (Amazon.com Inc.)"
)
public class PostMapperImpl implements PostMapper {

    @Override
    public Post dtoToEntity(CreatePostDto.Request request) {
        if ( request == null ) {
            return null;
        }

        Post.PostBuilder post = Post.builder();

        post.postType( request.postType() );
        post.title( request.title() );
        post.content( request.content() );
        List<Long> list = request.publicMembers();
        if ( list != null ) {
            post.publicMembers( new ArrayList<Long>( list ) );
        }
        List<Long> list1 = request.privateMembers();
        if ( list1 != null ) {
            post.privateMembers( new ArrayList<Long>( list1 ) );
        }

        return post.build();
    }

    @Override
    public CreatePostDto.Response dtoToCreatePostResponse(Post post) {
        if ( post == null ) {
            return null;
        }

        PostType postType = null;
        String title = null;
        String content = null;

        postType = post.getPostType();
        title = post.getTitle();
        content = post.getContent();

        CreatePostDto.Response response = new CreatePostDto.Response( postType, title, content );

        return response;
    }

    @Override
    public UploadPostMediaDto.Response dtoToUploadPostMediaResponse(String url) {
        if ( url == null ) {
            return null;
        }

        String url1 = null;

        url1 = url;

        UploadPostMediaDto.Response response = new UploadPostMediaDto.Response( url1 );

        return response;
    }

    @Override
    public GetPostListDto.Response dtoToGetPostListResponse(Post post) {
        if ( post == null ) {
            return null;
        }

        Long postId = null;
        String title = null;

        postId = post.getId();
        title = post.getTitle();

        boolean hasImage = false;
        boolean hasVideo = false;

        GetPostListDto.Response response = new GetPostListDto.Response( postId, title, hasImage, hasVideo );

        return response;
    }

    @Override
    public List<GetPostListDto.Response> dtoToGetPostListResponse(List<Post> postList) {
        if ( postList == null ) {
            return null;
        }

        List<GetPostListDto.Response> list = new ArrayList<GetPostListDto.Response>( postList.size() );
        for ( Post post : postList ) {
            list.add( dtoToGetPostListResponse( post ) );
        }

        return list;
    }

    @Override
    public List<PostDto.PostResponse> dtoToPostResponse(List<PostDto> postDtos) {
        if ( postDtos == null ) {
            return null;
        }

        List<PostDto.PostResponse> list = new ArrayList<PostDto.PostResponse>( postDtos.size() );
        for ( PostDto postDto : postDtos ) {
            list.add( postDtoToPostResponse( postDto ) );
        }

        return list;
    }

    protected PostDto.PostResponse postDtoToPostResponse(PostDto postDto) {
        if ( postDto == null ) {
            return null;
        }

        Long postId = null;
        String title = null;
        String createdDate = null;

        postId = postDto.getPostId();
        title = postDto.getTitle();
        if ( postDto.getCreatedDate() != null ) {
            createdDate = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format( postDto.getCreatedDate() );
        }

        boolean isPinned = false;
        boolean hasImage = false;
        boolean hasVideo = false;

        PostDto.PostResponse postResponse = new PostDto.PostResponse( postId, isPinned, title, createdDate, hasImage, hasVideo );

        return postResponse;
    }
}
