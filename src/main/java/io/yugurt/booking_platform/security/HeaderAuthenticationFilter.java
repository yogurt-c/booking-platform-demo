package io.yugurt.booking_platform.security;

import io.yugurt.booking_platform.domain.enums.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String userId = request.getHeader(USER_ID_HEADER);
        String roleStr = request.getHeader(USER_ROLE_HEADER);

        if (userId != null && roleStr != null) {
            try {
                UserRole role = UserRole.valueOf(roleStr);

                // UserContext에 저장 (AOP에서 사용)
                UserContext userContext = new UserContext(userId, role);
                UserContextHolder.setContext(userContext);

                // Spring Security Authentication 설정
                var authority = new SimpleGrantedAuthority("ROLE_" + role.name());
                var authentication = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    List.of(authority)
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("Authenticated user: {} with role: {}", userId, role);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid role in header: {}", roleStr);
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // 요청 처리 후 ThreadLocal 정리
            UserContextHolder.clear();
            SecurityContextHolder.clearContext();
        }
    }
}
