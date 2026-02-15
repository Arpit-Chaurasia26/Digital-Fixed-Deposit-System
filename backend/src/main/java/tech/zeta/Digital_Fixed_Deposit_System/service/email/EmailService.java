package tech.zeta.Digital_Fixed_Deposit_System.service.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Priyanshu Mishra
 */


@Slf4j
@Service
public class EmailService {


    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String to, String otp) {
        log.info("Sending OTP email to {}", to);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Digital FD | Email Verification OTP");
        message.setText(
                "Your OTP for Digital Fixed Deposit registration is:\n\n"
                        + otp
                        + "\n\nThis OTP is valid for 5 minutes.\n\n"
                        + "If you did not request this, please ignore this email."
                        + "\n\nBest regards,\nDigital Fixed Deposit Team"


        );

        mailSender.send(message);
        log.info("OTP email sent to {}", to);
    }
}


//   xufvbxsmunkuhhrb
