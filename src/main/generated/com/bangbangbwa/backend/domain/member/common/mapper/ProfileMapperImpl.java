package com.bangbangbwa.backend.domain.member.common.mapper;

import com.bangbangbwa.backend.domain.member.common.dto.ProfileDto;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-15T20:27:39+0900",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.13 (Amazon.com Inc.)"
)
public class ProfileMapperImpl implements ProfileMapper {

    @Override
    public ProfileDto.Response dtoToResponse(ProfileDto dto) {
        if ( dto == null ) {
            return null;
        }

        String imageUrl = null;
        String nickname = null;
        boolean isFollowing = false;
        String selfIntroduction = null;

        imageUrl = dto.getImageUrl();
        nickname = dto.getNickname();
        if ( dto.getIsFollowing() != null ) {
            isFollowing = dto.getIsFollowing();
        }
        selfIntroduction = dto.getSelfIntroduction();

        List<String> tags = selectTags(dto);

        ProfileDto.Response response = new ProfileDto.Response( imageUrl, nickname, isFollowing, selfIntroduction, tags );

        return response;
    }
}
