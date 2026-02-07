package tech.zeta.Digital_Fixed_Deposit_System.dto.auth;

//Jackson does not need setters, it automatically sets the fields through reflection
public class RegisterRequest {

    private String name;
    private String email;
    private String password;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
