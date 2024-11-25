package com.bangbangbwa.backend.domain.promotion.business;

import com.bangbangbwa.backend.domain.promotion.common.entity.Streamer;
import com.bangbangbwa.backend.domain.promotion.repository.StreamerRepository;
import com.bangbangbwa.backend.domain.streamer.common.exception.NotFoundStreamerException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StreamerReader {

  private final StreamerRepository streamerRepository;

  public List<Streamer> readAllStreamers() {
    return streamerRepository.findAll();
  }

  public Streamer findByMemberId(Long memberId) {
    return streamerRepository.findByMemberId(memberId)
        .orElseThrow(NotFoundStreamerException::new);
  }
}