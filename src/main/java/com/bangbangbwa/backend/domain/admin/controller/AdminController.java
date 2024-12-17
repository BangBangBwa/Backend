package com.bangbangbwa.backend.domain.admin.controller;


import com.bangbangbwa.backend.domain.admin.common.dto.ApproveStreamerDto;
import com.bangbangbwa.backend.domain.admin.common.dto.GetPendingMembers;
import com.bangbangbwa.backend.domain.admin.common.dto.GetPendingMembers.GetPendingMemberResponse;
import com.bangbangbwa.backend.domain.admin.common.dto.GetReportedPostsDto;
import com.bangbangbwa.backend.domain.admin.common.dto.GetReportedPostsDto.GetReportedPostsResponse;
import com.bangbangbwa.backend.domain.admin.common.dto.GetReportedPostsDto.Response;
import com.bangbangbwa.backend.domain.admin.common.dto.ResolveReportedPostDto;
import com.bangbangbwa.backend.domain.admin.service.AdminService;
import com.bangbangbwa.backend.domain.streamer.common.entity.PendingStreamer;
import com.bangbangbwa.backend.domain.streamer.common.mapper.PendingStreamerMapper;
import com.bangbangbwa.backend.global.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

  private final AdminService adminService;

  @PostMapping("/updatePendingStatus")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ApiResponse<ApproveStreamerDto.Response> updatePendingStatus(
      @RequestBody @Valid ApproveStreamerDto.Request request
  ){
    PendingStreamer pendingStreamer = adminService.updatePendingStatus(request);
    ApproveStreamerDto.Response response = PendingStreamerMapper
        .INSTANCE
        .dtoToApproveStreamerResponse(pendingStreamer);

    return ApiResponse.ok(response);
  }

  @GetMapping("/getPendingMembers")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ApiResponse<?> getPendingMembers() {
    List<GetPendingMemberResponse> getPendingMemberResponse = adminService.getPendingMembers();
    GetPendingMembers.Response response = new GetPendingMembers.Response(getPendingMemberResponse);
    return ApiResponse.ok(response);
  }

  @GetMapping("/getReportedPosts")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ApiResponse<GetReportedPostsDto.Response> getReportedPosts() {
    List<GetReportedPostsResponse> posts = adminService.getReportedPosts();
    GetReportedPostsDto.Response response = new Response(posts);
    return ApiResponse.ok(response);
  }

  @PostMapping("/ResolveReportedPost")
  public ApiResponse<?> resolveReportedPost(@RequestBody ResolveReportedPostDto.Request request) {
    adminService.resolveReportedPost(request);
    return ApiResponse.ok();
  }
}
