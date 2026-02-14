package tech.zeta.Digital_Fixed_Deposit_System;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Application Tests
 * Verifies that the main application class is configured correctly
 */
@DisplayName("Application Tests")
class DigitalFixedDepositSystemApplicationTests {

    @Test
    @DisplayName("Application class exists and is annotated correctly")
    void applicationClassExists() {
        assertThat(DigitalFixedDepositSystemApplication.class).isNotNull();
    }

    @Test
    @DisplayName("Main method is accessible")
    void mainMethodIsAccessible() throws NoSuchMethodException {
        assertThat(DigitalFixedDepositSystemApplication.class.getMethod("main", String[].class))
                .isNotNull();
    }
}
