package tech.zeta.Digital_Fixed_Deposit_System.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import tech.zeta.Digital_Fixed_Deposit_System.entity.transaction.Transaction;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("TransactionRepository Tests")
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository repository;

    @BeforeEach
    void clearData() {
        repository.deleteAll();
    }

    @Test
    void findByUserId_returnsMatches() {
        repository.save(tx(1L, 100L));
        repository.save(tx(2L, 200L));

        assertThat(repository.findByUserId(1L)).hasSize(1);
    }

    private Transaction tx(Long userId, Long fdId) {
        Transaction tx = new Transaction();
        tx.setUserId(userId);
        tx.setFdId(fdId);
        tx.setAmountPaid(new BigDecimal("1000.00"));
        tx.setInterestPaid(new BigDecimal("50.00"));
        return tx;
    }
}
