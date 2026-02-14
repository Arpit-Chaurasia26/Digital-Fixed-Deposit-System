/*
Author : Priyanshu Mishra
*/
package tech.zeta.Digital_Fixed_Deposit_System.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Jackson sets fields via reflection; setters are not required
public class LoginRequest {

    private static final Logger logger = LogManager.getLogger(LoginRequest.class);

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password must not be blank")
    private String password;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
