package com.example.simplezakka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // フォームのPOSTエラー防止（開発中はOK）
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll() // すべてのリクエストを許可
            )
            .headers(headers -> headers
                .frameOptions().disable() // H2コンソール表示のためにframeを許可
            );

        return http.build();
    }
}
