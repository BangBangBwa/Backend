package com.bangbangbwa.backend.domain.admin.common.dto;

import com.bangbangbwa.backend.domain.sns.common.enums.ReportStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class ResolveReportedPostDto {

  @Schema(name = "ResolveReportedPostResponse", description = "신고된 게시물 처리 요청 DTO")
  public record Request(
      @Schema(description = "신고 ID", example = "1L")
      @NotNull(message = "신고 ID를 입력헤주세요.")
      Long reportPostId,
      @Schema(description = "상태 값", example = "CANCEL, PENDING, DELETED")
      @NotNull(message = "상태값을 입력헤주세요.")
      ReportStatus status
      ){ }

}
