package tech.zeta.Digital_Fixed_Deposit_System.service.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
Author : Priyanshu Mishra
*/


@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String to, String otp) {
        logger.info("Sending OTP email to {}", to);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Digital FD | Email Verification OTP");
        message.setText(
                "Your OTP for Digital Fixed Deposit registration is:\n\n"
                        + otp
                        + "\n\nThis OTP is valid for 5 minutes.\n\n"
                        + "If you did not request this, please ignore this email."
        );

        mailSender.send(message);
        logger.info("OTP email sent to {}", to);
    }
}


//   xufvbxsmunkuhhrb
