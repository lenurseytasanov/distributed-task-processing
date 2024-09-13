package edu.java.distributedfileprocessing.config;

import edu.java.distributedfileprocessing.service.SaveUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.savedrequest.NullRequestCache;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SaveUserService saveUserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/users/**", "/error", "/login").permitAll()
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout.logoutUrl("/api/v1/users/logout"))
                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(new Http403ForbiddenEntryPoint()))
                .oauth2Login(login -> login
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo.oidcUserService(saveUserService))
                ) // TODO не работает /login
                .requestCache(cache -> cache.requestCache(new NullRequestCache()))
                .build();
    }
}
