package tech.zeta.Digital_Fixed_Deposit_System.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/*
Author : Priyanshu Mishra
*/


@Component
public class CookieUtil {

    private static final Logger logger = LogManager.getLogger(CookieUtil.class);

    // Cookie Names
    private static final String ACCESS_TOKEN_COOKIE = "accessToken";
    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    // Expiry (seconds)
    private static final int ACCESS_TOKEN_MAX_AGE = 30 * 60;
    private static final int REFRESH_TOKEN_MAX_AGE = 7 * 24 * 60 * 60;

    //  SET 

    public void setAccessToken(HttpServletResponse response, String accessToken) {
        Cookie cookie = new Cookie(ACCESS_TOKEN_COOKIE, accessToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // true in production (HTTPS)
        cookie.setPath("/");
        cookie.setMaxAge(ACCESS_TOKEN_MAX_AGE);
        response.addCookie(cookie);
        logger.debug("Access token cookie set");
    }

    public void setRefreshToken(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // true in production (HTTPS)
        cookie.setPath("/auth"); // refresh only needed for auth
        cookie.setMaxAge(REFRESH_TOKEN_MAX_AGE);
        response.addCookie(cookie);
        logger.debug("Refresh token cookie set");
    }

    // CLEAR

    public void clearAuthCookies(HttpServletResponse response) {
        clearCookie(response, ACCESS_TOKEN_COOKIE, "/");
        clearCookie(response, REFRESH_TOKEN_COOKIE, "/auth");
        logger.debug("Auth cookies cleared");
    }

    private void clearCookie(HttpServletResponse response, String name, String path) {
        Cookie cookie = new Cookie(name, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath(path);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        logger.debug("Cookie cleared for name={} path={}", name, path);
    }

    //  EXTRACT

    public String extractRefreshToken(HttpServletRequest request) {
        if (request == null || request.getCookies() == null) {
            logger.debug("No cookies available to extract refresh token");
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if (REFRESH_TOKEN_COOKIE.equals(cookie.getName())) {
                logger.debug("Refresh token cookie found");
                return cookie.getValue();
            }
        }
        logger.debug("Refresh token cookie not found");
        return null;
    }
}
