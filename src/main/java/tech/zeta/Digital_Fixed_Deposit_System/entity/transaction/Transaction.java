package tech.zeta.Digital_Fixed_Deposit_System.entity.transaction;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "fd_transaction",
        indexes = {
        @Index(name = "idx_fd_user", columnList = "user_id"),
    }
)
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name="fd_id",nullable = false)
    private Long fdId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name="amount_paid",nullable = false, precision = 15, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "interest_paid", precision = 15, scale = 2)
    private BigDecimal interestPaid;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Long getFdId() {
        return fdId;
    }

    public void setFdId(Long fdId) {
        this.fdId = fdId;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public BigDecimal getInterestPaid() {
        return interestPaid;
    }

    public void setInterestPaid(BigDecimal interestPaid) {
        this.interestPaid = interestPaid;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }


}
