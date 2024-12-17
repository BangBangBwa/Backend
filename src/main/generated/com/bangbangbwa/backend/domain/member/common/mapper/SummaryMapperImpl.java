package com.bangbangbwa.backend.domain.member.common.mapper;

import com.bangbangbwa.backend.domain.member.common.dto.SummaryDto;
import com.bangbangbwa.backend.domain.promotion.common.dto.PlatformDto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-15T20:27:39+0900",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.13 (Amazon.com Inc.)"
)
public class SummaryMapperImpl implements SummaryMapper {

    @Override
    public SummaryDto.Response dtoToResponse(SummaryDto dto) {
        if ( dto == null ) {
            return null;
        }

        Long followerCount = null;
        Long followingCount = null;
        Long postCount = null;
        boolean isStreamer = false;
        boolean isSubmittedToStreamer = false;
        List<PlatformDto> platforms = null;

        followerCount = dto.getFollowerCount();
        followingCount = dto.getFollowingCount();
        postCount = dto.getPostCount();
        if ( dto.getIsStreamer() != null ) {
            isStreamer = dto.getIsStreamer();
        }
        if ( dto.getIsSubmittedToStreamer() != null ) {
            isSubmittedToStreamer = dto.getIsSubmittedToStreamer();
        }
        List<PlatformDto> list = dto.getPlatforms();
        if ( list != null ) {
            platforms = new ArrayList<PlatformDto>( list );
        }

        SummaryDto.Response response = new SummaryDto.Response( followerCount, followingCount, postCount, isStreamer, isSubmittedToStreamer, platforms );

        return response;
    }
}
