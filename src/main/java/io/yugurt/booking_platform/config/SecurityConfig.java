package io.yugurt.booking_platform.config;

import io.yugurt.booking_platform.security.HeaderAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // Accommodation APIs
                .requestMatchers(HttpMethod.POST, "/api/accommodations").hasRole("HOST")
                .requestMatchers(HttpMethod.GET, "/api/accommodations").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/accommodations/*").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/accommodations/*").hasRole("HOST")  // + @RequireOwner
                .requestMatchers(HttpMethod.DELETE, "/api/accommodations/*").hasRole("HOST")  // + @RequireOwner

                // Room APIs
                .requestMatchers(HttpMethod.POST, "/api/rooms").hasRole("HOST")
                .requestMatchers(HttpMethod.GET, "/api/rooms/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/accommodations/*/rooms").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/rooms/*").hasRole("HOST")  // + @RequireOwner (via accommodation)
                .requestMatchers(HttpMethod.DELETE, "/api/rooms/*").hasRole("HOST")  // + @RequireOwner (via accommodation)

                // Reservation APIs
                .requestMatchers(HttpMethod.POST, "/api/reservations").hasRole("GUEST")
                .requestMatchers(HttpMethod.GET, "/api/reservations")
                .hasAnyRole("GUEST", "HOST")  // + @RequireOwner or host check
                .requestMatchers(HttpMethod.GET, "/api/reservations/*")
                .hasAnyRole("GUEST", "HOST")  // + @RequireOwner or host check
                .requestMatchers(HttpMethod.DELETE, "/api/reservations/*").hasRole("GUEST")  // + @RequireOwner

                // ADMIN은 모든 요청 가능
                .requestMatchers("/api/**").hasRole("ADMIN")

                .anyRequest().authenticated()
            )
            .addFilterBefore(new HeaderAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
