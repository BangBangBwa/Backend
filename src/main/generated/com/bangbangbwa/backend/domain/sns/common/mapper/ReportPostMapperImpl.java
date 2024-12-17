package com.bangbangbwa.backend.domain.sns.common.mapper;

import com.bangbangbwa.backend.domain.sns.common.dto.ReportPostDto;
import com.bangbangbwa.backend.domain.sns.common.entity.ReportPost;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-17T21:01:17+0900",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.13 (Amazon.com Inc.)"
)
public class ReportPostMapperImpl implements ReportPostMapper {

    @Override
    public ReportPost dtoToEntity(ReportPostDto.Request request) {
        if ( request == null ) {
            return null;
        }

        ReportPost.ReportPostBuilder reportPost = ReportPost.builder();

        reportPost.postId( request.postId() );
        reportPost.reason( request.reason() );

        return reportPost.build();
    }
}
