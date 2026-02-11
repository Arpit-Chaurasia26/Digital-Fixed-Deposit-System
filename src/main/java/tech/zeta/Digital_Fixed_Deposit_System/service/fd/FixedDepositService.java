package tech.zeta.Digital_Fixed_Deposit_System.service.fd;

import tech.zeta.Digital_Fixed_Deposit_System.dto.fd.*;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.InterestScheme;
import tech.zeta.Digital_Fixed_Deposit_System.exception.BusinessException;
import tech.zeta.Digital_Fixed_Deposit_System.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;
import tech.zeta.Digital_Fixed_Deposit_System.repository.FixedDepositRepository;
import tech.zeta.Digital_Fixed_Deposit_System.util.DateUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class FixedDepositService {

    private static final BigDecimal MIN_FD_AMOUNT = new BigDecimal("1000");

    private final FixedDepositRepository fixedDepositRepository;
    private final InterestCalculationService interestCalculationService;

    public FixedDepositService(
            FixedDepositRepository fixedDepositRepository,
            InterestCalculationService interestCalculationService
    ) {
        this.fixedDepositRepository = fixedDepositRepository;
        this.interestCalculationService = interestCalculationService;
    }

    // Book a new Fixed Deposit.
    @Transactional
    public FixedDeposit bookFixedDeposit(Long userId, BookFDRequest request) {

        validateAmount(request.getAmount());

        InterestScheme scheme = request.getInterestScheme();
        if (scheme == null) {
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

        return fixedDepositRepository.save(fd);
    }

    // Fetch all Fixed Deposits of a user with dynamically calculated accrued interest.
    @Transactional(readOnly = true)
    public List<FixedDeposit> getFixedDepositsByUser(Long userId) {

        List<FixedDeposit> fds = fixedDepositRepository.findByUserId(userId);

        fds.forEach(this::enrichWithAccruedInterest);

        return fds;
    }

    // Fetch a specific Fixed Deposit of a user.
    @Transactional(readOnly = true)
    public FixedDeposit getFixedDepositById(Long userId, Long fdId) {

        FixedDeposit fd = fixedDepositRepository
                .findByIdAndUserId(fdId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Fixed Deposit not found for user"));

        enrichWithAccruedInterest(fd);
        return fd;
    }

    // Update Fixed Deposit Status of a particular FD of a user
    @Transactional
    public FixedDeposit updateFixedDepositStatus(
            Long fdId,
            FDStatus newStatus
    ) {
        FixedDeposit fd = fixedDepositRepository
                .findById(fdId)
                .orElseThrow(() -> new ResourceNotFoundException("Fixed Deposit not found for user"));

        FDStatus currentStatus = fd.getStatus();

        // ACTIVE -> MATURED
        if (currentStatus == FDStatus.ACTIVE && newStatus == FDStatus.MATURED) {
            if (LocalDate.now().isBefore(fd.getMaturityDate())) {
                throw new BusinessException("FD has not yet reached maturity date");
            }
            fd.setStatus(FDStatus.MATURED);
            return fixedDepositRepository.save(fd);
        }

        // ACTIVE -> BROKEN
        if (currentStatus == FDStatus.ACTIVE && newStatus == FDStatus.BROKEN) {
            if (!fd.getInterestScheme().isPrematureBreakAllowed()) {
                throw new BusinessException("Premature withdrawal is not allowed for this FD scheme");
            }
            if (!LocalDate.now().isBefore(fd.getMaturityDate())) {
                throw new BusinessException("FD has already matured; use withdrawal instead");
            }
            fd.setStatus(FDStatus.BROKEN);
            return fixedDepositRepository.save(fd);
        }

        // MATURED -> CLOSED
        if (currentStatus == FDStatus.MATURED && newStatus == FDStatus.CLOSED) {
            if (LocalDate.now().isBefore(fd.getMaturityDate())) {
                throw new BusinessException("FD cannot be closed before maturity date");
            }
            fd.setStatus(FDStatus.CLOSED);
            return fixedDepositRepository.save(fd);
        }

        // INVALID Transition
        throw new BusinessException(String.format("Invalid FD status transition from %s to %s", currentStatus, newStatus));
    }


    @Transactional(readOnly = true)
    public List<FixedDeposit> getFixedDepositsByFilters(
            Long userId, FDStatus status, BigDecimal minAmount, BigDecimal maxAmount) {
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
        return fds;
    }

    // Fetch fixed deposits maturing within N days
    @Transactional(readOnly = true)
    public List<FDMaturityResponse> getUserFDsMaturingWithinDays(Long userId, int days) {
        if (days <= 0) throw new BusinessException("Days must be positive");

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);

        List<FixedDeposit> fds =
            fixedDepositRepository.findByUserIdAndMaturityDateBetween(userId, today, endDate)
                .stream()
                .filter(fd -> fd.getStatus() == FDStatus.ACTIVE || fd.getStatus() == FDStatus.MATURED)
                .toList();

        return mapToMaturityResponse(fds, today);
    }

    // Fetch all fixed deposits maturing within N days
    @Transactional(readOnly = true)
    public List<FDMaturityResponse> getAllFDsMaturingWithinDays(int days) {
        if (days <= 0) throw new BusinessException("Days must be positive");

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);

        List<FixedDeposit> fds =
            fixedDepositRepository.findByMaturityDateBetween(today, endDate)
                .stream()
                .filter(fd -> fd.getStatus() == FDStatus.ACTIVE || fd.getStatus() == FDStatus.MATURED)
                .toList();

        return mapToMaturityResponse(fds, today);
    }

    // Fetch financial year fixed deposit summary analytics for user
    @Transactional(readOnly = true)
    public FDFinancialYearSummaryResponse getUserFinancialYearSummary(Long userId, Integer year) {
        int fy = (year != null) ? year : DateUtils.getCurrentFinancialYear();

        LocalDate start = DateUtils.getFinancialYearStart(fy);
        LocalDate end = DateUtils.getFinancialYearEnd(fy);

        java.time.LocalDateTime startDateTime = start.atStartOfDay();
        java.time.LocalDateTime endDateTime = end.atTime(23, 59, 59, 999_999_999);

        List<FixedDeposit> fds =
            fixedDepositRepository.findByUserIdAndCreatedAtBetween(userId, startDateTime, endDateTime);

        return buildFinancialYearSummary(fds, fy);
    }

    // Fetch financial year fixed deposit summary analytics for admin
    @Transactional(readOnly = true)
    public FDFinancialYearSummaryResponse getAdminFinancialYearSummary(Integer year) {
        int fy = (year != null) ? year : DateUtils.getCurrentFinancialYear();

        LocalDate start = DateUtils.getFinancialYearStart(fy);
        LocalDate end = DateUtils.getFinancialYearEnd(fy);

        java.time.LocalDateTime startDateTime = start.atStartOfDay();
        java.time.LocalDateTime endDateTime = end.atTime(23, 59, 59, 999_999_999);

        List<FixedDeposit> fds = fixedDepositRepository.findByCreatedAtBetween(startDateTime, endDateTime);

        return buildFinancialYearSummary(fds, fy);
    }

    // Fetch all fixed deposits created in a financial year (admin)
    @Transactional(readOnly = true)
    public List<FixedDeposit> getAdminFDsByFinancialYear(Integer year) {
        int fy = (year != null) ? year : DateUtils.getCurrentFinancialYear();

        LocalDate start = DateUtils.getFinancialYearStart(fy);
        LocalDate end = DateUtils.getFinancialYearEnd(fy);

        java.time.LocalDateTime startDateTime = start.atStartOfDay();
        java.time.LocalDateTime endDateTime = end.atTime(23, 59, 59, 999_999_999);

        List<FixedDeposit> fds = fixedDepositRepository.findByCreatedAtBetween(startDateTime, endDateTime);
        fds.forEach(this::enrichWithAccruedInterest);
        return fds;
    }

        // Fetch all fixed deposits in chronological order (admin)
        @Transactional(readOnly = true)
        public List<FixedDeposit> getAllFDsChronological(String order) {
            Sort sort = "asc".equalsIgnoreCase(order)
                    ? Sort.by("createdAt").ascending()
                    : Sort.by("createdAt").descending();

            List<FixedDeposit> fds = fixedDepositRepository.findAll(sort);
            fds.forEach(this::enrichWithAccruedInterest);
            return fds;
        }

    // Fetch user fixed deposit portfolio
    @Transactional(readOnly = true)
    public FDPortfolioResponse getUserFDPortfolio(Long userId) {
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


    @Transactional(readOnly = true)
    public FixedDeposit getFixedDepositByIdForAdmin(Long fdId) {

        return fixedDepositRepository
                .findById(fdId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Fixed Deposit not found with id: " + fdId)
                );
    }



    // Fetch FD interest accrual timeline
    @Transactional(readOnly = true)
    public FDInterestTimelineResponse getInterestTimeline(FixedDeposit fd, String interval) {
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


    // Get current accrued interest for a Fixed Deposit of a particular user
    @Transactional(readOnly = true)
    public FDInterestResponse getCurrentInterestForUser(Long userId, Long fdId) {
        FixedDeposit fd = fixedDepositRepository
                .findByIdAndUserId(fdId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Fixed Deposit not found for user")
                );

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

    private void enrichWithAccruedInterest(FixedDeposit fd) {

        if (fd.getStatus() == FDStatus.ACTIVE || fd.getStatus() == FDStatus.MATURED) {

            fd.setAccruedInterest(interestCalculationService.calculateAccruedInterest(fd));
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(MIN_FD_AMOUNT) < 0) {
            throw new BusinessException("Minimum Fixed Deposit amount is " + MIN_FD_AMOUNT);
        }
    }

    private FDFinancialYearSummaryResponse buildFinancialYearSummary(List<FixedDeposit> fds, int financialYear) {
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

    private FixedDeposit buildInterestSnapshot(FixedDeposit original, LocalDate asOfDate) {
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


    private List<FDMaturityResponse> mapToMaturityResponse(List<FixedDeposit> fds, LocalDate today) {
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
