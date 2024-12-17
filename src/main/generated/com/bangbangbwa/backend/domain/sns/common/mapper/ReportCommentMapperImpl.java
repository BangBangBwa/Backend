package com.bangbangbwa.backend.domain.sns.common.mapper;

import com.bangbangbwa.backend.domain.sns.common.dto.ReportCommentDto;
import com.bangbangbwa.backend.domain.sns.common.entity.ReportComment;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-16T16:40:29+0900",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.13 (Amazon.com Inc.)"
)
public class ReportCommentMapperImpl implements ReportCommentMapper {

    @Override
    public ReportComment dtoToEntity(ReportCommentDto.Request request) {
        if ( request == null ) {
            return null;
        }

        ReportComment.ReportCommentBuilder reportComment = ReportComment.builder();

        reportComment.commentId( request.commentId() );

        return reportComment.build();
    }
}
