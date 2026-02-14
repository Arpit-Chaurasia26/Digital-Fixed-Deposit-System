package tech.zeta.Digital_Fixed_Deposit_System.entity.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
Author : Priyanshu Mishra
*/


public enum Role {
    USER,
    ADMIN;

    private static final Logger logger = LogManager.getLogger(Role.class);

    public static Role fromString(String value) {
        if (value == null) {
            logger.warn("Role value is null");
            throw new IllegalArgumentException("Role value must not be null");
        }
        try {
            return Role.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            logger.warn("Invalid role value: {}", value);
            throw ex;
        }
    }
}
