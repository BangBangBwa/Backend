package com.bangbangbwa.backend.domain.sns.common.entity;

import com.bangbangbwa.backend.domain.sns.common.enums.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

@Alias("reportPost")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportPost {

    private Long id;
    private ReportStatus status;
    private Long postId;
    private String reason;
    private String createdId;
    private LocalDateTime createdAt;
    private String updatedId;
    private LocalDateTime updatedAt;

    @Builder
    public ReportPost(Long postId, String reason, String createdId) {
        this.status = ReportStatus.PENDING;
        this.postId = postId;
        this.reason = reason;
        this.createdId = createdId;
        this.createdAt = LocalDateTime.now();
    }

    public void updateCreatedId(Long memberId) {
        this.createdId = memberId.toString();
    }

    public void updateStatus(ReportStatus status, Long updatedId) {
        this.status = status;
        this.updatedId = updatedId.toString();
        this.updatedAt = LocalDateTime.now();
    }
}
