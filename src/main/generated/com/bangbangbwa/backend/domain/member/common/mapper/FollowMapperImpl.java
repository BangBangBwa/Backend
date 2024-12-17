package com.bangbangbwa.backend.domain.member.common.mapper;

import com.bangbangbwa.backend.domain.member.common.dto.ToggleFollowDto;
import com.bangbangbwa.backend.domain.member.common.entity.Follow;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-17T21:01:17+0900",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.13 (Amazon.com Inc.)"
)
public class FollowMapperImpl implements FollowMapper {

    @Override
    public Follow dtoToEntity(ToggleFollowDto.Request dto) {
        if ( dto == null ) {
            return null;
        }

        Follow.FollowBuilder follow = Follow.builder();

        follow.followeeId( dto.memberId() );

        return follow.build();
    }
}
