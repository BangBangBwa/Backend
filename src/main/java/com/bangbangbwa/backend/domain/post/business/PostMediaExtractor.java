package com.bangbangbwa.backend.domain.post.business;

import com.bangbangbwa.backend.domain.post.common.enums.MediaType;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

@Component
public class PostMediaExtractor {

  private static final Set<String> IMAGE_EXTENSIONS =
      Set.of("jpg", "jpeg", "png", "gif", "bmp", "webp", "tiff");
  private static final Set<String> VIDEO_EXTENSIONS =
      Set.of("mp4", "avi", "mkv", "mov", "flv", "wmv");

  public MediaType getMediaType(String content) {
    Set<String> extensions = extractFileExtensions(content);

    boolean isPhoto = extensions.stream().anyMatch(IMAGE_EXTENSIONS::contains);
    boolean isVideo = extensions.stream().anyMatch(VIDEO_EXTENSIONS::contains);

    return (isPhoto && isVideo) ? MediaType.BOTH :
        (isPhoto) ? MediaType.PHOTO :
            (isVideo) ? MediaType.VIDEO : null;
  }

  private Set<String> extractFileExtensions(String content) {
    return Stream.concat(
            extractExtensions(content, "img[src]"),
            extractExtensions(content, "video source[src]")
        )
        .collect(Collectors.toSet());
  }

  private Stream<String> extractExtensions(String content, String selector) {
    return Jsoup.parse(content).select(selector).stream()
        .map(element -> getFileExtension(element.attr("src")))
        .filter(extension -> !extension.isEmpty());
  }

  private String getFileExtension(String url) {
    int lastIndexOfDot = url.lastIndexOf('.');
    return (lastIndexOfDot == -1) ? "" : url.substring(lastIndexOfDot + 1).toLowerCase();
  }
}
