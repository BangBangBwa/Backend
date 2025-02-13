package com.bangbangbwa.backend.domain.promotion.common.entity;

import com.bangbangbwa.backend.domain.platform.common.entity.Platform;
import com.bangbangbwa.backend.domain.tag.common.entity.Tag;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@Alias("streamer")
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Streamer {

  @PostConstruct
  private void init() {
    tags = new ArrayList<>();
    platforms = new ArrayList<>();
  }

  private Long id;
  private Long memberId;
  private List<Tag> tags;
  private List<Platform> platforms;

  @Builder
  public Streamer(
      Long memberId
  ) {
    this.memberId = memberId;
  }
}
