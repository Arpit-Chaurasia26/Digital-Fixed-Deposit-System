package tech.zeta.Digital_Fixed_Deposit_System.service.support.specifications;

import org.springframework.data.jpa.domain.Specification;
import tech.zeta.Digital_Fixed_Deposit_System.entity.support.SupportTicket;
import tech.zeta.Digital_Fixed_Deposit_System.entity.support.TicketStatus;
import java.time.LocalDateTime;

public class SupportTicketSpecifications {

    public static Specification<SupportTicket> hasStatus(TicketStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<SupportTicket> createdAfter(LocalDateTime fromDate) {
        return (root, query, cb) -> fromDate == null ? null : cb.greaterThanOrEqualTo(root.get("createdTime"), fromDate);
    }

    public static Specification<SupportTicket> createdBefore(LocalDateTime toDate) {
        return (root, query, cb) -> toDate == null ? null : cb.lessThanOrEqualTo(root.get("createdTime"), toDate);
    }

    public static Specification<SupportTicket> updatedAfter(LocalDateTime fromDate) {
        return (root, query, cb) -> fromDate == null ? null : cb.greaterThanOrEqualTo(root.get("updatedTime"), fromDate);
    }

    public static Specification<SupportTicket> updatedBefore(LocalDateTime toDate) {
        return (root, query, cb) -> toDate == null ? null : cb.lessThanOrEqualTo(root.get("updatedTime"), toDate);
    }

    // 🔹 NEW: Filter by userId
    public static Specification<SupportTicket> hasUserId(Long userId) {
        return (root, query, cb) -> userId == null ? null : cb.equal(root.get("userId"), userId);
    }

    // 🔹 NEW: Filter by fdId
    public static Specification<SupportTicket> hasFdId(Long fdId) {
        return (root, query, cb) -> fdId == null ? null : cb.equal(root.get("fdId"), fdId);
    }
}
