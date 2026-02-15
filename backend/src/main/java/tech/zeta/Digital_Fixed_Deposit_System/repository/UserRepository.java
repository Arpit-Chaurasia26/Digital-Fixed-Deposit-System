package tech.zeta.Digital_Fixed_Deposit_System.repository;

import tech.zeta.Digital_Fixed_Deposit_System.entity.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
Author : Priyanshu Mishra
*/


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Logger logger = LogManager.getLogger(UserRepository.class);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);


}
