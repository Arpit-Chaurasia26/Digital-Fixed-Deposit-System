package tech.zeta.Digital_Fixed_Deposit_System.config.security;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tech.zeta.Digital_Fixed_Deposit_System.entity.auth.RefreshToken;
import tech.zeta.Digital_Fixed_Deposit_System.entity.user.Role;
import tech.zeta.Digital_Fixed_Deposit_System.entity.user.User;
import tech.zeta.Digital_Fixed_Deposit_System.repository.UserRepository;
import tech.zeta.Digital_Fixed_Deposit_System.service.auth.RefreshTokenService;
import tech.zeta.Digital_Fixed_Deposit_System.service.auth.TokenService;
import tech.zeta.Digital_Fixed_Deposit_System.util.CookieUtil;

import java.io.IOException;

/**
 * @author Priyanshu Mishra
 */

@Component
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;
    private final CookieUtil cookieUtil;

    public OAuthSuccessHandler(
            UserRepository userRepository,
            TokenService tokenService,
            RefreshTokenService refreshTokenService,
            CookieUtil cookieUtil
    ) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.refreshTokenService = refreshTokenService;
        this.cookieUtil = cookieUtil;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String providerId=oauthUser.getAttribute("sub");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser=User
                            .builder()
                            .name(name)
                            .email(email)
                            .password("OAUTH_USER_PROVIDEED_BY_GOOGLE")
                            .authProvider("GOOGLE")
                            .providerId(providerId)
                            .emailVerified(true)
                            .role(Role.USER)
                            .build();

                    return userRepository.save(newUser);
                });

        // Ensure role is set (fallback to USER if null)
        Role userRole = user.getRole() != null ? user.getRole() : Role.USER;

        String accessToken =
                tokenService.generateAccessToken(
                        user.getId(),
                        userRole.name()
                );

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user.getId());

        // Set authentication cookies
        cookieUtil.setAccessToken(response, accessToken);
        cookieUtil.setRefreshToken(response, refreshToken.getToken());

        // Redirect to frontend OAuth success page
        String redirectUrl = "http://localhost:3000/oauth/callback";
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
