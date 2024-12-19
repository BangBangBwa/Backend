package com.bangbangbwa.backend.domain.comment.service;

import com.bangbangbwa.backend.domain.comment.business.CommentReader;
import com.bangbangbwa.backend.domain.comment.business.CommentUpdater;
import com.bangbangbwa.backend.domain.comment.common.dto.AddCommentReplyDto;
import com.bangbangbwa.backend.domain.comment.common.dto.MyPostCommentDto.MyPostCommentResponse;
import com.bangbangbwa.backend.domain.member.business.MemberProvider;
import com.bangbangbwa.backend.domain.member.common.dto.CommentDto.CommentResponse;
import com.bangbangbwa.backend.domain.sns.common.entity.Comment;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentReader commentReader;
  private final CommentUpdater commentUpdater;
  private final MemberProvider memberProvider;

  public void addCommentReply(AddCommentReplyDto.Request request) {
    Long commentId = request.commentId();
    String replyMessage = request.message();

    Comment comment = commentReader.findById(commentId);
    comment.updateReplyComment(replyMessage);
    commentUpdater.updateForAddReply(comment);
  }

  public List<MyPostCommentResponse> myPostCommentList(Long postId) {
    return commentReader.findCommentListByPostId(postId);
  }

  // TODO : xml 파일 추가 구현 예정.
  public List<CommentResponse> myPageCommentList() {
    Long memberId = memberProvider.getCurrentMemberId();
    return commentReader.findCommentListByMemberId(memberId);
  }
}
