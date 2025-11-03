package com.website.security.jwt;

import com.website.entity.User;
import com.website.user.dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws InsufficientAuthenticationException, ServletException, IOException {
        if (request.getRequestURI().startsWith("/auth")) {
            try {
                String authorization = request.getHeader("Authorization");
                if (authorization == null || !authorization.startsWith("Bearer ")) {
                    System.out.println("token이 없거나, Bearer가 포함되어 있지 않습니다.");
                    throw new InsufficientAuthenticationException("JWT 토큰 없음 또는 잘못된 형식");
                }
                String token = authorization.split(" ")[1];
                if (jwtUtil.isExpired(token)) {
                    System.out.println("token Expire 상태입니다.");
                    throw new AccessDeniedException("JWT 만료됨");
                }

                Long userCode = jwtUtil.getUserCode(token);
                String userName = jwtUtil.getUsername(token);
                String role = jwtUtil.getRole(token);

                User user = new User();
                user.setUserCode(userCode);
                user.setName(userName);
                user.setRole(role);
                CustomUserDetails customUserDetails = new CustomUserDetails(user);

                Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authToken);

                filterChain.doFilter(request, response);
            } catch (AuthenticationException | AccessDeniedException e) {
                SecurityContextHolder.clearContext();
                // 직접 401 처리
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter()
                        .write("{\"error\":\"인증 실패\",\"message\":\"로그인이 필요합니다.\"}");
            }
        } else {
            filterChain.doFilter(request, response);
            return;
        }
    }
}
