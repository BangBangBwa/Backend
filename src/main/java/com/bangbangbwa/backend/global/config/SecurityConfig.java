package com.bangbangbwa.backend.global.config;

import com.bangbangbwa.backend.global.config.log.LogFilter;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return web -> web.ignoring() // Security ignore
        .requestMatchers("/h2-console/**", "/favicon.ico");
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http
        .csrf(AbstractHttpConfigurer::disable) // csrf 비활성화
        .cors(Customizer.withDefaults()) // cors 비활성화
        .httpBasic(AbstractHttpConfigurer::disable) // 기본 인증 로그인 비활성화
        .formLogin(AbstractHttpConfigurer::disable) // 기본 login form 비활성화
        .logout(AbstractHttpConfigurer::disable) // 기본 logout 비활성화
        .headers(c -> c
            .frameOptions(
                HeadersConfigurer.FrameOptionsConfig::sameOrigin)) // X-Frame-Options sameOrigin 제한
        .sessionManagement(c -> c
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 비활성화

        // 로깅 필터 추가
        .addFilterBefore(new LogFilter(), SecurityContextHolderFilter.class)
    ;

    return http.build();
  }
}