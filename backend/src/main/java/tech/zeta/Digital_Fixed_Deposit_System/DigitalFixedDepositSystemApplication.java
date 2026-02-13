package tech.zeta.Digital_Fixed_Deposit_System;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Author - Arpit Chaurasia
@SpringBootApplication
@EnableScheduling
public class DigitalFixedDepositSystemApplication {

    private static final Logger logger = LoggerFactory.getLogger(DigitalFixedDepositSystemApplication.class);

    public static void main(String[] args) {
        logger.info("Starting Digital Fixed Deposit System Application");
        SpringApplication.run(DigitalFixedDepositSystemApplication.class, args);
        logger.info("Digital Fixed Deposit System Application started successfully");
    }
}
