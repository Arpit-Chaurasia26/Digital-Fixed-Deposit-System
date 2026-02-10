package tech.zeta.Digital_Fixed_Deposit_System.dto.auth;

public class AuthResponse {

    private final String accessToken;

    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
