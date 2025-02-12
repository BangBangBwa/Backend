package com.bangbangbwa.backend.domain.sns.common.mapper;

import com.bangbangbwa.backend.domain.sns.common.dto.ReportPostDto;
import com.bangbangbwa.backend.domain.post.common.entity.ReportPost;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReportPostMapper {

  ReportPostMapper INSTANCE = Mappers.getMapper(ReportPostMapper.class);

  @Mapping(target = "postId", source = "request.postId")
  @Mapping(target = "reason", source = "request.reason")
  ReportPost dtoToEntity(ReportPostDto.Request request);

}
