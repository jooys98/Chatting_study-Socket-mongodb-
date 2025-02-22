package com.example.chatting_study.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class JWTCheckFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("------------------JWTCheckFilter.................");
        log.info("request.getServletPath(): {}", request.getServletPath());
        log.info("..................................................");

        String path = request.getRequestURI();
        log.info("check uri: " + path);

        // /api/member/로 시작하는 요청은 필터를 타지 않도록 설정
        if (path.startsWith("/api/member/login") || path.startsWith("/api/member/join") ||
                path.startsWith("/ws-stomp")) {
            filterChain.doFilter(request, response);
            return;
        }

        String autHeaderStr = request.getHeader("Authorization");
        log.info("autHeaderStr Authorization: {}", autHeaderStr);

        if ((Objects.equals(autHeaderStr, "Bearer null") || (autHeaderStr == null)) && (
                request.getServletPath().startsWith("/api/product/")
                        //리뷰 관련 api 상품 별 리뷰는 필터 안타게
                        || request.getServletPath().matches("^/api/review/list/\\d+$")
                        // shop 관련 api
                        || request.getServletPath().matches("/api/shop/\\d+$")
                        // community 관련 api
                        || request.getServletPath().startsWith("/api/community/")
        )) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Bearer accessToken 형태로 전달되므로 Bearer 제거
            String accessToken = autHeaderStr.substring(7);// Bearer 제거
            // 쿠키로 가져와
            log.info("JWTCheckFilter accessToken: {}", accessToken);

            Map<String, Object> claims = jwtUtil.validateToken(accessToken);

            log.info("JWT claims: {}", claims);

            MemberDTO memberDTO = (MemberDTO) userDetailsService.loadUserByUsername((String) claims.get("email"));

            log.info("memberDTO: {}", memberDTO);
            log.info("memberDto.getAuthorities(): {}", memberDTO.getAuthorities());

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(memberDTO, memberDTO.getPassword(), memberDTO.getAuthorities());

            // SecurityContextHolder에 인증 객체 저장
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            // 다음 필터로 이동
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("JWT Check Error...........");
            log.error("e.getMessage(): {}", e.getMessage());

            ObjectMapper objectMapper = new ObjectMapper();
            String msg = objectMapper.writeValueAsString(Map.of("error", "ERROR_ACCESS_TOKEN"));

            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            PrintWriter printWriter = response.getWriter();
            printWriter.println(msg);
            printWriter.close();
        }
    }
}