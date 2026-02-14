/*
Author : Priyanshu Mishra
*/
package tech.zeta.Digital_Fixed_Deposit_System.entity.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_otp")
@Getter
@Setter
public class EmailOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String otp;

    private LocalDateTime expiresAt;

    private boolean verified;

}


