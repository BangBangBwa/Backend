package com.bangbangbwa.backend.domain.post.business;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.bangbangbwa.backend.domain.post.common.enums.MediaType;
import com.bangbangbwa.backend.global.test.ServiceTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

class PostMediaExtractorTest extends ServiceTest {

  @InjectMocks
  private PostMediaExtractor postMediaExtractor;

  @Test
  void getMediaType_문자열() {
    MediaType mediaType = postMediaExtractor.getMediaType("a");
    assertThat(mediaType).isNull();
  }

  @Test
  void getMediaType_사진() {
    MediaType mediaType = postMediaExtractor.getMediaType(
        "<p><img src=\"https://bbb-s3.s3.amazonaws.com/ba4b4e86-3b3d-4f30-878"
            + "7-6943f5e67374.png\" alt=\"앱아이콘 1.png\" "
            + "contenteditable=\"false\"><br></p>");

    assertThat(mediaType).isEqualTo(MediaType.PHOTO);
  }

  @Test
  void getMediaType_영상() {
    MediaType mediaType = postMediaExtractor.getMediaType(
        "<p><img src=\"https://bbb-s3.s3.amazonaws.com/ba4b4e86-"
            + "3b3d-4f30-8787-6943f5e67374.mp4\" alt=\"앱아이콘 1.png\" contenteditable="
            + "\"false\"><br></p>");
    assertThat(mediaType).isEqualTo(MediaType.VIDEO);
  }

  @Test
  void getMediaType_사진_영상() {
    MediaType mediaType = postMediaExtractor.getMediaType(
        "<p><img src=\"https://bbb-s3.s3.amazonaws.com/ba4b4e86-3b3d-4f30-8787-6943f5e67374.png\" alt=\"앱아이콘 1.png\" contenteditable=\"false\"></p>\n"
            + "    <p>\n"
            + "        <video controls>\n"
            + "            <source src=\"https://www.w3schools.com/html/movie.mp4\" type=\"video/mp4\">\n"
            + "            Your browser does not support the video tag.\n"
            + "        </video>\n"
            + "    </p>"
    );
    assertThat(mediaType).isEqualTo(MediaType.BOTH);
  }
}