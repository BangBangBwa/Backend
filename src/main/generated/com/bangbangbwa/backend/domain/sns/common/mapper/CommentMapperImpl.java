package com.bangbangbwa.backend.domain.sns.common.mapper;

import com.bangbangbwa.backend.domain.sns.common.dto.CreateCommentDto;
import com.bangbangbwa.backend.domain.sns.common.entity.Comment;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-15T20:27:39+0900",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.13 (Amazon.com Inc.)"
)
public class CommentMapperImpl implements CommentMapper {

    @Override
    public Comment dtoToEntity(CreateCommentDto.Request request) {
        if ( request == null ) {
            return null;
        }

        Comment.CommentBuilder comment = Comment.builder();

        comment.postId( request.postId() );
        comment.content( request.content() );

        return comment.build();
    }

    @Override
    public CreateCommentDto.Response dtoToCreateCommentResponse(Comment comment) {
        if ( comment == null ) {
            return null;
        }

        Long postId = null;
        String content = null;

        postId = comment.getPostId();
        content = comment.getContent();

        CreateCommentDto.Response response = new CreateCommentDto.Response( postId, content );

        return response;
    }
}
