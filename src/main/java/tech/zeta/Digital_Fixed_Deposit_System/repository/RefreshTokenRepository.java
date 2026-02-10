package tech.zeta.Digital_Fixed_Deposit_System.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.zeta.Digital_Fixed_Deposit_System.entity.auth.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByUserId(Long userId);
}
