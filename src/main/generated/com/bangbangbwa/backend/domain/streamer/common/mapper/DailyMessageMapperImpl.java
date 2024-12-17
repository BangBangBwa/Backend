package com.bangbangbwa.backend.domain.streamer.common.mapper;

import com.bangbangbwa.backend.domain.streamer.common.dto.CreateDailyMessageDto;
import com.bangbangbwa.backend.domain.streamer.common.entity.DailyMessage;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-15T20:27:38+0900",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.13 (Amazon.com Inc.)"
)
public class DailyMessageMapperImpl implements DailyMessageMapper {

    @Override
    public DailyMessage dtoToEntity(CreateDailyMessageDto.Request request) {
        if ( request == null ) {
            return null;
        }

        DailyMessage.DailyMessageBuilder dailyMessage = DailyMessage.builder();

        dailyMessage.message( request.message() );

        return dailyMessage.build();
    }

    @Override
    public CreateDailyMessageDto.Response dtoToPromoteStreamerResponse(DailyMessage dailyMessage) {
        if ( dailyMessage == null ) {
            return null;
        }

        String message = null;

        message = dailyMessage.getMessage();

        CreateDailyMessageDto.Response response = new CreateDailyMessageDto.Response( message );

        return response;
    }
}
