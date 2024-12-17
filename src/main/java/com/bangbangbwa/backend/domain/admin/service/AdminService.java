package com.bangbangbwa.backend.domain.admin.service;

import com.bangbangbwa.backend.domain.admin.business.AdminProvider;
import com.bangbangbwa.backend.domain.admin.common.dto.ApproveStreamerDto;
import com.bangbangbwa.backend.domain.admin.common.dto.GetPendingMembers.GetPendingMemberResponse;
import com.bangbangbwa.backend.domain.admin.common.dto.GetReportedPostsDto.GetReportedPostsResponse;
import com.bangbangbwa.backend.domain.admin.common.dto.ResolveReportedPostDto;
import com.bangbangbwa.backend.domain.admin.common.entity.Admin;
import com.bangbangbwa.backend.domain.member.business.MemberProvider;
import com.bangbangbwa.backend.domain.sns.business.ReportPostReader;
import com.bangbangbwa.backend.domain.sns.business.ReportPostUpdater;
import com.bangbangbwa.backend.domain.sns.common.entity.ReportPost;
import com.bangbangbwa.backend.domain.sns.common.enums.ReportStatus;
import com.bangbangbwa.backend.domain.streamer.common.business.PendingStreamerGenerator;
import com.bangbangbwa.backend.domain.streamer.common.business.PendingStreamerReader;
import com.bangbangbwa.backend.domain.streamer.common.business.PendingStreamerUpdater;
import com.bangbangbwa.backend.domain.streamer.common.entity.PendingStreamer;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

  private final PendingStreamerUpdater pendingStreamerUpdater;
  private final PendingStreamerGenerator pendingStreamerGenerator;
  private final PendingStreamerReader pendingStreamerReader;
  private final AdminProvider adminProvider;
  private final ReportPostReader reportPostReader;
  private final ReportPostUpdater reportPostUpdater;
  private final MemberProvider memberProvider;

  public PendingStreamer updatePendingStatus(ApproveStreamerDto.Request request) {
    Admin admin = adminProvider.getCurrentAdmin();
    PendingStreamer pendingStreamer = pendingStreamerGenerator.updateAdminId(request, admin);
    pendingStreamerUpdater.updateAdminId(pendingStreamer);
    return pendingStreamer;
  }

  public List<GetPendingMemberResponse> getPendingMembers() {
    return pendingStreamerReader.findByPendingMembers();
  }

  public List<GetReportedPostsResponse> getReportedPosts() {
    return reportPostReader.findAllPendingReports();
  }

  public void resolveReportedPost(ResolveReportedPostDto.Request request) {
    Long memberId = memberProvider.getCurrentMemberId();
    ReportPost reportPost = reportPostReader.findById(request.reportPostId());
    reportPost.updateStatus(request.status(), memberId);
    if (request.status() == ReportStatus.DELETED)
    reportPostUpdater.updateStatus(reportPost);
  }
}
