package tech.zeta.Digital_Fixed_Deposit_System.dto.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.zeta.Digital_Fixed_Deposit_System.entity.user.Role;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/*
Author : Priyanshu Mishra
*/


@DisplayName("UserProfileResponse DTO Tests")
class UserProfileResponseTest {

    @Test
    void getters_andToString_work() {
        Instant created = Instant.now();
        UserProfileResponse dto = new UserProfileResponse(1L, "User", "u@example.com", Role.USER, created);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("User");
        assertThat(dto.getEmail()).isEqualTo("u@example.com");
        assertThat(dto.getRole()).isEqualTo(Role.USER);
        assertThat(dto.getCreatedAt()).isEqualTo(created);
        assertThat(dto.toString()).contains("u@example.com").contains("USER");
    }
}

