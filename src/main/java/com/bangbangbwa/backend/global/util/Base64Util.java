package com.bangbangbwa.backend.global.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class Base64Util {

  public static String encode(String data) {
    return Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
  }

  public static String decode(String encodedData) {
    return new String(Base64.getDecoder().decode(encodedData));
  }
}
