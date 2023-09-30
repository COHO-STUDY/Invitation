package com.coho.invitation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http
                .csrf(c -> c.disable())     // csrf 비활성화
                .authorizeHttpRequests(auth ->
                    auth    // requestMatcher의 인자로 전달된 url은 모두에게 허용
                            .requestMatchers("/","/swagger-ui/**","/api-docs/**").permitAll()  // Swagger 설정(/swagger-ui/** : url, /api-docs/** : 리소스)
                            .requestMatchers("/api/members/kakao/web","/api/members/kakao/android/").permitAll()    // 로그인(회원가입)
                            .anyRequest().authenticated()   // 그 외 모든 요청 인증이 필요함
                )
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )   // 세션을 사용하지 않기 때문에 session 생성 정책을 STATELESS로 설정
                .build();

    }
}