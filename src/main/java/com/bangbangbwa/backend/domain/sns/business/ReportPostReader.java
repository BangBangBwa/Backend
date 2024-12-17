package com.bangbangbwa.backend.domain.sns.business;

import com.bangbangbwa.backend.domain.admin.common.dto.GetReportedPostsDto.GetReportedPostsResponse;
import com.bangbangbwa.backend.domain.sns.common.entity.ReportPost;
import com.bangbangbwa.backend.domain.sns.common.enums.ReportStatus;
import com.bangbangbwa.backend.domain.sns.exception.NotFoundReportException;
import com.bangbangbwa.backend.domain.sns.repository.ReportPostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReportPostReader {

    private final ReportPostRepository reportPostRepository;

    public Optional<ReportPost> findPendingReportByPostIdAndCreatedId(Long postId, Long createdId) {
        return reportPostRepository.findByPostIdAndCreatedId(postId, createdId.toString(), ReportStatus.PENDING);
    }

    public List<GetReportedPostsResponse> findAllPendingReports() {
        return reportPostRepository.findAllPendingReports();
    }

    public ReportPost findById(Long id) {
        return reportPostRepository.findById(id).orElseThrow(NotFoundReportException::new);
    }
}
