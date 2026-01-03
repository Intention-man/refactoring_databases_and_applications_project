package com.example.prac.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/actors/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/actor-experiments/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/actor-tasks/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/documents/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/equipments/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/experiments/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/modules/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/projects/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/project-equipment/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/resources/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/space-stations/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/systems/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/tasks/**").authenticated()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/actors/**").hasRole("SYSADMIN")
                        .requestMatchers("/api/actor-experiments/**").hasRole("MANAGER")
                        .requestMatchers("/api/actor-tasks/**").hasRole("MANAGER")
                        .requestMatchers("/api/documents/**").hasRole("SCIENTIST")
                        .requestMatchers("/api/equipments/**").hasAnyRole("ENGINEER", "LOGISTICIAN", "SCIENTIST")
                        .requestMatchers("/api/experiments/**").hasRole("MANAGER")
                        .requestMatchers("/api/modules/**").hasRole("ENGINEER")
                        .requestMatchers("/api/projects/**").hasRole("MANAGER")
                        .requestMatchers("/api/project-equipment/**").hasAnyRole("MANAGER", "ENGINEER", "LOGISTICIAN", "SCIENTIST")
                        .requestMatchers("/api/resources/**").hasRole("LOGISTICIAN")
                        .requestMatchers("/api/space-stations/**").hasRole("MANAGER")
                        .requestMatchers("/api/systems/**").hasRole("ENGINEER")
                        .requestMatchers("/api/tasks/**").hasRole("MANAGER")
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:4200"); // Local Dev Server for FE
        configuration.addAllowedOrigin("http://localhost:4201"); // Local Nginx for FE
        configuration.addAllowedOrigin("https://se.ifmo.ru/");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
