/*
Author : Priyanshu Mishra
*/
package tech.zeta.Digital_Fixed_Deposit_System.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpdateUserProfileRequest {

    private static final Logger logger = LogManager.getLogger(UpdateUserProfileRequest.class);

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    public UpdateUserProfileRequest() {
    }

    public UpdateUserProfileRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UpdateUserProfileRequest{name='" + name + "', email='" + email + "'}";
    }
}