package tech.zeta.Digital_Fixed_Deposit_System.config.security;

import tech.zeta.Digital_Fixed_Deposit_System.service.auth.TokenService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * @author Priyanshu Mishra
 */


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LogManager.getLogger(JwtAuthenticationFilter.class);
    private static final String ACCESS_TOKEN_COOKIE = "accessToken";
    private final TokenService tokenService;

    public JwtAuthenticationFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = null;

        // Try to get token from Authorization header first
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        // If no token in header, try to get from cookie
        if (token == null) {
            token = extractTokenFromCookie(request);
        }

        // No token found → continue filter chain
        if (token == null) {
            logger.debug("No access token found for request path={}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Validate access token (throws if invalid/expired)
            Claims claims = tokenService.validateAccessToken(token);

            Long userId = tokenService.extractUserId(claims);
            String role = tokenService.extractRole(claims);

            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority("ROLE_" + role);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            List.of(authority)
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            logger.debug("Security context set for user id={}", userId);

        } catch (Exception ex) {
            // Invalid or expired token → ignore & continue
            SecurityContextHolder.clearContext();
            logger.warn("Access token rejected");
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (ACCESS_TOKEN_COOKIE.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
