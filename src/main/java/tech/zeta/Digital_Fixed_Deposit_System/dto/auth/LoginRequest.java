package tech.zeta.Digital_Fixed_Deposit_System.dto.auth;

//Jackson does not need setters, it automatically sets the fields through reflection
public class LoginRequest {

    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
