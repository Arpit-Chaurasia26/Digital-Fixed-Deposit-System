/*
Author : Priyanshu Mishra
*/
package tech.zeta.Digital_Fixed_Deposit_System.config.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LogManager.getLogger(JwtAuthenticationEntryPoint.class);

    // This method is called whenever an unauthenticated user tries to access a protected resource.
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {

        logger.warn("Unauthorized access to path={}", request.getRequestURI());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        response.getWriter().write("""
            {
              "message": "Unauthorized: Authentication token is missing or invalid",
              "status": 401
            }
        """);
    }
}
