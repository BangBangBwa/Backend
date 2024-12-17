package com.bangbangbwa.backend.domain.sns.repository;

import com.bangbangbwa.backend.domain.admin.common.dto.GetReportedPostsDto.GetReportedPostsResponse;
import com.bangbangbwa.backend.domain.sns.common.entity.ReportPost;
import com.bangbangbwa.backend.domain.sns.common.enums.ReportStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReportPostRepository {

    private final SqlSession mysql;

    public void save(ReportPost reportPost) {
        mysql.insert("ReportPostMapper.save", reportPost);
    }

    public Optional<ReportPost> findByPostIdAndCreatedId(Long postId, String createdId, ReportStatus status) {
        Map<String, Object> params = new HashMap<>();
        params.put("postId", postId);
        params.put("createdId", createdId);
        params.put("status", status);
        return Optional.ofNullable(mysql.selectOne("ReportPostMapper.findByPostIdAndCreatedIdAndStatus", params));
    }

    public List<GetReportedPostsResponse> findAllPendingReports() {
        return mysql.selectList("ReportPostMapper.findAllPendingReports");
    }

    public Optional<ReportPost> findById(Long id) {
        return Optional.ofNullable(mysql.selectOne("ReportPostMapper.findById", id));
    }
}
