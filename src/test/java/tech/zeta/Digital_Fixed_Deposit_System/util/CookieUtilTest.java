package tech.zeta.Digital_Fixed_Deposit_System.util;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

public class CookieUtilTest {

    private final CookieUtil cookieUtil = new CookieUtil();

    @Test
    void setAccessToken_setsCookieWithExpectedNameAndValue() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        cookieUtil.setAccessToken(response, "access-token");

        Cookie accessCookie = response.getCookie("accessToken");
        assertNotNull(accessCookie);
        assertEquals("access-token", accessCookie.getValue());
        assertEquals("/", accessCookie.getPath());
        assertTrue(accessCookie.isHttpOnly());
    }

    @Test
    void setRefreshToken_setsCookieWithExpectedNameAndValue() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        cookieUtil.setRefreshToken(response, "refresh-token");

        Cookie refreshCookie = response.getCookie("refreshToken");
        assertNotNull(refreshCookie);
        assertEquals("refresh-token", refreshCookie.getValue());
        assertEquals("/auth", refreshCookie.getPath());
        assertTrue(refreshCookie.isHttpOnly());
    }

    @Test
    void clearAuthCookies_marksCookiesExpired() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        cookieUtil.clearAuthCookies(response);

        Cookie accessCookie = response.getCookie("accessToken");
        Cookie refreshCookie = response.getCookie("refreshToken");
        assertNotNull(accessCookie);
        assertNotNull(refreshCookie);
        assertEquals(0, accessCookie.getMaxAge());
        assertEquals(0, refreshCookie.getMaxAge());
    }

    @Test
    void extractRefreshToken_returnsTokenWhenPresent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("refreshToken", "rt"));

        String token = cookieUtil.extractRefreshToken(request);

        assertEquals("rt", token);
    }

    @Test
    void extractRefreshToken_returnsNullWhenMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        String token = cookieUtil.extractRefreshToken(request);

        assertNull(token);
    }
}
