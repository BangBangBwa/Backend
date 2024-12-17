package com.bangbangbwa.backend.domain.streamer.common.mapper;

import com.bangbangbwa.backend.domain.admin.common.dto.ApproveStreamerDto;
import com.bangbangbwa.backend.domain.member.common.dto.PromoteStreamerDto;
import com.bangbangbwa.backend.domain.streamer.common.entity.PendingStreamer;
import com.bangbangbwa.backend.domain.streamer.common.enums.PendingType;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-15T20:27:39+0900",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.13 (Amazon.com Inc.)"
)
public class PendingStreamerMapperImpl implements PendingStreamerMapper {

    @Override
    public PendingStreamer dtoToEntity(PromoteStreamerDto.Request request) {
        if ( request == null ) {
            return null;
        }

        PendingStreamer.PendingStreamerBuilder pendingStreamer = PendingStreamer.builder();

        pendingStreamer.platformUrl( request.platformUrl() );

        return pendingStreamer.build();
    }

    @Override
    public PendingStreamer dtoToEntity(ApproveStreamerDto.Request request) {
        if ( request == null ) {
            return null;
        }

        PendingStreamer.PendingStreamerBuilder pendingStreamer = PendingStreamer.builder();

        pendingStreamer.id( request.pendingStreamerId() );
        if ( request.status() != null ) {
            pendingStreamer.status( Enum.valueOf( PendingType.class, request.status() ) );
        }

        return pendingStreamer.build();
    }

    @Override
    public PromoteStreamerDto.Response dtoToPromoteStreamerResponse(PendingStreamer pendingStreamer) {
        if ( pendingStreamer == null ) {
            return null;
        }

        String platformUrl = null;

        platformUrl = pendingStreamer.getPlatformUrl();

        PromoteStreamerDto.Response response = new PromoteStreamerDto.Response( platformUrl );

        return response;
    }

    @Override
    public ApproveStreamerDto.Response dtoToApproveStreamerResponse(PendingStreamer pendingStreamer) {
        if ( pendingStreamer == null ) {
            return null;
        }

        Long pendingStreamerId = null;

        pendingStreamerId = pendingStreamer.getId();

        ApproveStreamerDto.Response response = new ApproveStreamerDto.Response( pendingStreamerId );

        return response;
    }
}
