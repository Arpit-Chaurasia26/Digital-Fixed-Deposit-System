package tech.zeta.Digital_Fixed_Deposit_System;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * @author Arpit Chaurasia
 */
@Slf4j
@SpringBootApplication
@EnableScheduling
public class DigitalFixedDepositSystemApplication {

    public static void main(String[] args) {
        log.info("Starting Digital Fixed Deposit System Application");
        SpringApplication.run(DigitalFixedDepositSystemApplication.class, args);
        log.info("Digital Fixed Deposit System Application started successfully");
    }
}
