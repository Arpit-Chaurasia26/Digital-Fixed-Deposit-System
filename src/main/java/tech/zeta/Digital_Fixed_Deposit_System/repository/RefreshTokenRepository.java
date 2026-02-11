package tech.zeta.Digital_Fixed_Deposit_System.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.zeta.Digital_Fixed_Deposit_System.entity.auth.RefreshToken;

import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {

    Logger logger = LogManager.getLogger(RefreshTokenRepository.class);

    Optional<RefreshToken> findByToken(String token);

    void deleteByUserId(Long userId);
}
