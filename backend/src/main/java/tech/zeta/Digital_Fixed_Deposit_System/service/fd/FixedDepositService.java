package tech.zeta.Digital_Fixed_Deposit_System.service.fd;

import lombok.extern.slf4j.Slf4j;
import tech.zeta.Digital_Fixed_Deposit_System.dto.fd.*;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.InterestScheme;
import tech.zeta.Digital_Fixed_Deposit_System.exception.BusinessException;
import tech.zeta.Digital_Fixed_Deposit_System.exception.ResourceNotFoundException;
import tech.zeta.Digital_Fixed_Deposit_System.service.fd.status.FDStatusTransitionHandler;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;
import tech.zeta.Digital_Fixed_Deposit_System.repository.FixedDepositRepository;
import tech.zeta.Digital_Fixed_Deposit_System.util.DateUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Arpit Chaurasia
 */
@Slf4j
@Service
public class FixedDepositService {

    private static final BigDecimal MIN_FD_AMOUNT = new BigDecimal("5000");

    private final FixedDepositRepository fixedDepositRepository;
    private final InterestCalculationService interestCalculationService;
    private final List<FDStatusTransitionHandler> statusTransitionHandlers;

    public FixedDepositService(
            FixedDepositRepository fixedDepositRepository,
            InterestCalculationService interestCalculationService,
            List<FDStatusTransitionHandler> statusTransitionHandlers
    ) {
        this.fixedDepositRepository = fixedDepositRepository;
        this.interestCalculationService = interestCalculationService;
        this.statusTransitionHandlers = statusTransitionHandlers;
    }

    /**
     * @author Yogendra Kavuru
     */
    // Book a new Fixed Deposit.
    @Transactional
    public FixedDeposit bookFixedDeposit(Long userId, BookFDRequest request) {
        log.info("Booking fixed deposit: userId={}, amount={}, scheme={}", userId, request.getAmount(), request.getInterestScheme());

        validateAmount(request.getAmount());

        InterestScheme scheme = request.getInterestScheme();
        if (scheme == null) {
            log.warn("Booking failed: missing interest scheme for userId={}", userId);
            throw new BusinessException("Interest scheme must be selected");
        }

        LocalDate startDate = LocalDate.now();
        LocalDate maturityDate = startDate.plusMonths(scheme.getTenureInMonths());

        FixedDeposit fd = new FixedDeposit();

        fd.setUserId(userId);
        fd.setAmount(request.getAmount());
        fd.setInterestScheme(scheme);
        fd.setInterestRate(scheme.getAnnualInterestRate());
        fd.setTenureMonths(scheme.getTenureInMonths());
        fd.setStartDate(startDate);
        fd.setMaturityDate(maturityDate);
        fd.setStatus(FDStatus.ACTIVE);
        fd.setAccruedInterest(BigDecimal.ZERO);

        FixedDeposit saved = fixedDepositRepository.save(fd);
        log.info("Fixed deposit booked: fdId={}, userId={}", saved.getId(), userId);
        return saved;
    }

    /**
     * @author Yogendra Kavuru
     */
    // Fetch all Fixed Deposits of a user with dynamically calculated accrued interest.
    @Transactional(readOnly = true)
    public List<FixedDeposit> getFixedDepositsByUser(Long userId) {
        log.info("Fetching FDs for user: userId={}", userId);

        List<FixedDeposit> fds = fixedDepositRepository.findByUserId(userId);

        fds.forEach(this::enrichWithAccruedInterest);

        log.debug("Fetched FDs for user: userId={}, count={}", userId, fds.size());
        return fds;
    }

    /**
     * @author Yogendra Kavuru
     */
    // Fetch a specific Fixed Deposit of a user.
    @Transactional(readOnly = true)
    public FixedDeposit getFixedDepositById(Long userId, Long fdId) {
        log.info("Fetching FD by id for user: userId={}, fdId={}", userId, fdId);

        FixedDeposit fd = fixedDepositRepository
                .findByIdAndUserId(fdId, userId)
                .orElseThrow(() -> {
                    log.warn("Fixed deposit not found for user: userId={}, fdId={}", userId, fdId);
                    return new ResourceNotFoundException("Fixed Deposit not found for user");
                });


        enrichWithAccruedInterest(fd);
        return fd;
    }

    /**
     * @author Arpit Chaurasia
     */
    // Update Fixed Deposit Status of a particular FD of a user
    @Transactional
    public FixedDeposit updateFixedDepositStatus(
            Long fdId,
            FDStatus newStatus
    ) {
        log.info("Updating FD status: fdId={}, newStatus={}", fdId, newStatus);
        FixedDeposit fd = fixedDepositRepository
                .findById(fdId)
                .orElseThrow(() -> {
                    log.warn("Fixed deposit not found for status update: fdId={}", fdId);
                    return new ResourceNotFoundException("Fixed Deposit not found for user");
                });

        FDStatus currentStatus = fd.getStatus();

        FDStatusTransitionHandler handler = statusTransitionHandlers.stream()
                .filter(h -> h.supports(currentStatus, newStatus))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Invalid FD status transition: fdId={}, from={}, to={}", fdId, currentStatus, newStatus);
                    return new BusinessException(String.format(
                            "Invalid FD status transition from %s to %s", currentStatus, newStatus
                    ));
                });

        handler.apply(fd);
        return fixedDepositRepository.save(fd);
    }


    /**
     * @author Arpit Chaurasia
     */
    @Transactional(readOnly = true)
    public List<FixedDeposit> getFixedDepositsByFilters(
            Long userId, FDStatus status, BigDecimal minAmount, BigDecimal maxAmount) {
        log.info("Fetching FDs with filters: userId={}, status={}, minAmount={}, maxAmount={}",
                userId, status, minAmount, maxAmount);

        BigDecimal effectiveMin = (minAmount != null) ? minAmount : BigDecimal.ZERO;

        BigDecimal effectiveMax = (maxAmount != null) ? maxAmount : BigDecimal.valueOf(Long.MAX_VALUE);

        boolean hasAmountFilter = minAmount != null || maxAmount != null;

        List<FixedDeposit> fds;

        if (status != null && hasAmountFilter) {
            fds = fixedDepositRepository
                    .findByUserIdAndStatusAndAmountBetween(userId, status, effectiveMin, effectiveMax);
        } else if (status != null) {
            fds = fixedDepositRepository.findByUserIdAndStatus(userId, status);
        } else if (hasAmountFilter) {
            fds = fixedDepositRepository
                    .findByUserIdAndAmountBetween(userId, effectiveMin, effectiveMax);
        } else {
            fds = fixedDepositRepository.findByUserId(userId);
        }

        fds.forEach(this::enrichWithAccruedInterest);
        log.debug("Fetched FDs with filters: userId={}, count={}", userId, fds.size());
        return fds;
    }

    /**
     * @author Arpit Chaurasia
     */
    // Fetch fixed deposits maturing within N days
    @Transactional(readOnly = true)
    public List<FDMaturityResponse> getUserFDsMaturingWithinDays(Long userId, int days) {
        log.info("Fetching user maturing FDs: userId={}, days={}", userId, days);

        if (days <= 0) {
            log.warn("Invalid days parameter for user maturing FDs: userId={}, days={}", userId, days);
            throw new BusinessException("Days must be positive");
        }

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);

        List<FixedDeposit> fds =
            fixedDepositRepository.findByUserIdAndMaturityDateBetween(userId, today, endDate)
                .stream()
                .filter(fd -> fd.getStatus() == FDStatus.ACTIVE || fd.getStatus() == FDStatus.MATURED)
                .toList();

        return mapToMaturityResponse(fds, today);
    }

    /**
     * @author Arpit Chaurasia
     */
    // Fetch all fixed deposits maturing within N days
    @Transactional(readOnly = true)
    public List<FDMaturityResponse> getAllFDsMaturingWithinDays(int days) {
        log.info("Fetching all maturing FDs: days={}", days);

        if (days <= 0) {
            log.warn("Invalid days parameter for all maturing FDs: days={}", days);
            throw new BusinessException("Days must be positive");
        }

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);

        List<FixedDeposit> fds =
            fixedDepositRepository.findByMaturityDateBetween(today, endDate)
                .stream()
                .filter(fd -> fd.getStatus() == FDStatus.ACTIVE || fd.getStatus() == FDStatus.MATURED)
                .toList();

        return mapToMaturityResponse(fds, today);
    }

    /**
     * @author Arpit Chaurasia
     */
    // Fetch financial year fixed deposit summary analytics for user
    @Transactional(readOnly = true)
    public FDFinancialYearSummaryResponse getUserFinancialYearSummary(Long userId, Integer year) {
        log.info("Fetching user financial year summary: userId={}, year={}", userId, year);

        int fy = (year != null) ? year : DateUtils.getCurrentFinancialYear();

        LocalDate start = DateUtils.getFinancialYearStart(fy);
        LocalDate end = DateUtils.getFinancialYearEnd(fy);

        java.time.LocalDateTime startDateTime = start.atStartOfDay();
        java.time.LocalDateTime endDateTime = end.atTime(23, 59, 59, 999_999_999);

        List<FixedDeposit> fds =
            fixedDepositRepository.findByUserIdAndCreatedAtBetween(userId, startDateTime, endDateTime);

        return buildFinancialYearSummary(fds, fy);
    }

    /**
     * @author Arpit Chaurasia
     */
    // Fetch financial year fixed deposit summary analytics for admin
    @Transactional(readOnly = true)
    public FDFinancialYearSummaryResponse getAdminFinancialYearSummary(Integer year) {
        log.info("Fetching admin financial year summary: year={}", year);

        int fy = (year != null) ? year : DateUtils.getCurrentFinancialYear();

        LocalDate start = DateUtils.getFinancialYearStart(fy);
        LocalDate end = DateUtils.getFinancialYearEnd(fy);

        java.time.LocalDateTime startDateTime = start.atStartOfDay();
        java.time.LocalDateTime endDateTime = end.atTime(23, 59, 59, 999_999_999);

        List<FixedDeposit> fds = fixedDepositRepository.findByCreatedAtBetween(startDateTime, endDateTime);

        return buildFinancialYearSummary(fds, fy);
    }

    /**
     * @author Arpit Chaurasia
     */
    // Fetch all fixed deposits created in a financial year (admin)
    @Transactional(readOnly = true)
    public List<FixedDeposit> getAdminFDsByFinancialYear(Integer year) {
        log.info("Fetching admin FDs by financial year: year={}", year);

        int fy = (year != null) ? year : DateUtils.getCurrentFinancialYear();

        LocalDate start = DateUtils.getFinancialYearStart(fy);
        LocalDate end = DateUtils.getFinancialYearEnd(fy);

        java.time.LocalDateTime startDateTime = start.atStartOfDay();
        java.time.LocalDateTime endDateTime = end.atTime(23, 59, 59, 999_999_999);

        List<FixedDeposit> fds = fixedDepositRepository.findByCreatedAtBetween(startDateTime, endDateTime);
        fds.forEach(this::enrichWithAccruedInterest);
        log.debug("Fetched admin FDs by financial year: year={}, count={}", year, fds.size());
        return fds;
    }

    /**
     * @author Arpit Chaurasia
     */
    // Fetch all fixed deposits in chronological order (admin)
    @Transactional(readOnly = true)
    public List<FixedDeposit> getAllFDsChronological(String order) {
        log.info("Fetching all FDs chronological: order={}", order);

        Sort sort = "asc".equalsIgnoreCase(order)
                ? Sort.by("createdAt").ascending()
                : Sort.by("createdAt").descending();

        List<FixedDeposit> fds = fixedDepositRepository.findAll(sort);
        fds.forEach(this::enrichWithAccruedInterest);
        log.debug("Fetched all FDs chronological: count={}", fds.size());
        return fds;
    }

    /**
     * @author Arpit Chaurasia
     */
    // Fetch user fixed deposit portfolio
    @Transactional(readOnly = true)
    public FDPortfolioResponse getUserFDPortfolio(Long userId) {
        log.info("Fetching user FD portfolio: userId={}", userId);

        List<FixedDeposit> fds = fixedDepositRepository.findByUserId(userId);

        long totalFDs = fds.size();

        long activeFDs = fds.stream()
                        .filter(fd -> fd.getStatus() == FDStatus.ACTIVE)
                        .count();

        long maturedFDs = fds.stream()
                        .filter(fd -> fd.getStatus() == FDStatus.MATURED)
                        .count();

        long brokenFDs = fds.stream()
                        .filter(fd -> fd.getStatus() == FDStatus.BROKEN)
                        .count();

        BigDecimal totalPrincipal = fds.stream()
                        .map(FixedDeposit::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalInterest = fds.stream()
                        .map(interestCalculationService::calculateAccruedInterest)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDate nextMaturityDate = fds.stream()
                        .filter(fd -> fd.getStatus() == FDStatus.ACTIVE)
                        .map(FixedDeposit::getMaturityDate)
                        .min(LocalDate::compareTo)
                        .orElse(null);

        return new FDPortfolioResponse(
                totalFDs,
                activeFDs,
                maturedFDs,
                brokenFDs,
                totalPrincipal,
                totalInterest,
                nextMaturityDate
        );
    }


    /**
     * @author Arpit Chaurasia
     */
    @Transactional(readOnly = true)
    public FixedDeposit getFixedDepositByIdForAdmin(Long fdId) {
        log.info("Fetching FD by id for admin: fdId={}", fdId);

        return fixedDepositRepository
                .findById(fdId)
                .orElseThrow(() -> {
                    log.warn("Fixed deposit not found for admin: fdId={}", fdId);
                    return new ResourceNotFoundException("Fixed Deposit not found with id: " + fdId);
                });
    }


    /**
     * @author Arpit Chaurasia
     */
    // Fetch FD interest accrual timeline
    @Transactional(readOnly = true)
    public FDInterestTimelineResponse getInterestTimeline(FixedDeposit fd, String interval) {
        log.info("Fetching interest timeline: fdId={}, interval={}", fd.getId(), interval);

        List<InterestTimelinePoint> timeline = new ArrayList<>();

        LocalDate startDate = fd.getStartDate();

        LocalDate endDate =
                LocalDate.now().isAfter(fd.getMaturityDate()) ? fd.getMaturityDate() : LocalDate.now();

        LocalDate cursor = startDate;

        while (cursor.isBefore(endDate)) {

            LocalDate nextDate;
            String label;

            if ("YEARLY".equalsIgnoreCase(interval)) {
                nextDate = cursor.plusYears(1);
                label = String.valueOf(cursor.getYear());
            } else {
                nextDate = cursor.plusMonths(1);
                label = cursor.getYear() + "-" + String.format("%02d", cursor.getMonthValue());
            }

            if (nextDate.isAfter(endDate)) {
                nextDate = endDate;
            }

            // Snapshot created via helper method
            FixedDeposit snapshot = buildInterestSnapshot(fd, nextDate);

            BigDecimal accruedInterest = interestCalculationService.calculateAccruedInterest(snapshot);

            timeline.add(new InterestTimelinePoint(label, accruedInterest));

            cursor = nextDate;
        }

        return new FDInterestTimelineResponse(
                fd.getId(),
                fd.getAmount(),
                fd.getInterestRate(),
                interval,
                timeline
        );
    }

    /**
     * @author Arpit Chaurasia
     */
    // Get current accrued interest for a Fixed Deposit of a particular user
    @Transactional(readOnly = true)
    public FDInterestResponse getCurrentInterestForUser(Long userId, Long fdId) {
        log.info("Fetching current interest: userId={}, fdId={}", userId, fdId);

        FixedDeposit fd = fixedDepositRepository
                .findByIdAndUserId(fdId, userId)
                .orElseThrow(() -> {
                    log.warn("Fixed deposit not found for user interest: userId={}, fdId={}", userId, fdId);
                    return new ResourceNotFoundException("Fixed Deposit not found for user");
                });

        BigDecimal accruedInterest = interestCalculationService.calculateAccruedInterest(fd);

        return new FDInterestResponse(
                fd.getId(),
                fd.getStatus(),
                fd.getAmount(),
                accruedInterest,
                fd.getInterestRate(),
                fd.getInterestScheme().getInterestFrequency(),
                fd.getStartDate(),
                fd.getMaturityDate()
        );
    }





    // Helper methods

    /**
     * @author Arpit Chaurasia
     */
    private void enrichWithAccruedInterest(FixedDeposit fd) {
        log.debug("Enriching accrued interest: fdId={}, status={}", fd.getId(), fd.getStatus());

        if (fd.getStatus() == FDStatus.ACTIVE || fd.getStatus() == FDStatus.MATURED) {

            fd.setAccruedInterest(interestCalculationService.calculateAccruedInterest(fd));
        }
    }

    /**
     * @author Arpit Chaurasia
     */
    private void validateAmount(BigDecimal amount) {
        log.debug("Validating FD amount: amount={}", amount);

        if (amount == null || amount.compareTo(MIN_FD_AMOUNT) < 0) {
            log.warn("Invalid FD amount: amount={}, min={}", amount, MIN_FD_AMOUNT);
            throw new BusinessException("Minimum Fixed Deposit amount is " + MIN_FD_AMOUNT);
        }
    }

    /**
     * @author Arpit Chaurasia
     */
    private FDFinancialYearSummaryResponse buildFinancialYearSummary(List<FixedDeposit> fds, int financialYear) {
        log.debug("Building financial year summary: year={}, count={}", financialYear, fds.size());

        long totalFDs = fds.size();

        BigDecimal totalPrincipal =
                fds.stream()
                        .map(FixedDeposit::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalInterest =
                fds.stream()
                        .map(interestCalculationService::calculateAccruedInterest)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new FDFinancialYearSummaryResponse(
                financialYear + "-" + (financialYear + 1),
                totalFDs,
                totalPrincipal,
                totalInterest
        );
    }

    /**
     * @author Arpit Chaurasia
     */
    private FixedDeposit buildInterestSnapshot(FixedDeposit original, LocalDate asOfDate) {
        log.debug("Building interest snapshot: fdId={}, asOfDate={}", original.getId(), asOfDate);

        FixedDeposit snapshot = new FixedDeposit();

        snapshot.setAmount(original.getAmount());
        snapshot.setInterestRate(original.getInterestRate());
        snapshot.setInterestScheme(original.getInterestScheme());
        snapshot.setStartDate(original.getStartDate());

        // cap calculation at "asOfDate"
        snapshot.setMaturityDate(asOfDate);

        // Always ACTIVE for calculation purposes
        snapshot.setStatus(FDStatus.ACTIVE);

        return snapshot;
    }


    /**
     * @author Arpit Chaurasia
     */
    private List<FDMaturityResponse> mapToMaturityResponse(List<FixedDeposit> fds, LocalDate today) {
        log.debug("Mapping maturity response: count={}, today={}", fds.size(), today);

        if (fds.isEmpty()) {
            log.debug("No FDs found for maturity response on date={}", today);
        }
        return fds.stream()
                .map(fd -> new FDMaturityResponse(
                        fd.getId(),
                        fd.getUserId(),
                        fd.getAmount(),
                        fd.getMaturityDate(),
                        java.time.temporal.ChronoUnit.DAYS.between(
                                today, fd.getMaturityDate()
                        ),
                        fd.getStatus()
                ))
                .toList();
    }

}
