package com.bangbangbwa.backend.domain.member.common.mapper;

import com.bangbangbwa.backend.domain.member.common.dto.MemberLoginDto;
import com.bangbangbwa.backend.domain.member.common.dto.MemberSignupDto;
import com.bangbangbwa.backend.domain.member.common.entity.Member;
import com.bangbangbwa.backend.domain.sns.common.dto.SearchMemberDto;
import com.bangbangbwa.backend.domain.token.common.TokenDto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-16T16:40:29+0900",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.13 (Amazon.com Inc.)"
)
public class MemberMapperImpl implements MemberMapper {

    @Override
    public Member dtoToEntity(MemberSignupDto.Request request) {
        if ( request == null ) {
            return null;
        }

        Member.MemberBuilder member = Member.builder();

        member.nickname( request.nickname() );
        member.usageAgree( request.usageAgree() );
        member.personalAgree( request.personalAgree() );
        member.withdrawalAgree( request.withdrawalAgree() );

        return member.build();
    }

    @Override
    public MemberSignupDto.Response dtoToSignupResponse(TokenDto token) {
        if ( token == null ) {
            return null;
        }

        String accessToken = null;
        String refreshToken = null;

        accessToken = token.getAccessToken();
        refreshToken = token.getRefreshToken();

        MemberSignupDto.Response response = new MemberSignupDto.Response( accessToken, refreshToken );

        return response;
    }

    @Override
    public MemberLoginDto.Response dtoToLoginResponse(TokenDto token) {
        if ( token == null ) {
            return null;
        }

        String accessToken = null;
        String refreshToken = null;

        accessToken = token.getAccessToken();
        refreshToken = token.getRefreshToken();

        MemberLoginDto.Response response = new MemberLoginDto.Response( accessToken, refreshToken );

        return response;
    }

    @Override
    public SearchMemberDto.Response dtoToSearchNicknameResponse(Member member) {
        if ( member == null ) {
            return null;
        }

        Long memberId = null;
        String nickname = null;

        memberId = member.getId();
        nickname = member.getNickname();

        SearchMemberDto.Response response = new SearchMemberDto.Response( memberId, nickname );

        return response;
    }

    @Override
    public List<SearchMemberDto.Response> dtoToSearchNicknameResponse(List<Member> members) {
        if ( members == null ) {
            return null;
        }

        List<SearchMemberDto.Response> list = new ArrayList<SearchMemberDto.Response>( members.size() );
        for ( Member member : members ) {
            list.add( dtoToSearchNicknameResponse( member ) );
        }

        return list;
    }
}
