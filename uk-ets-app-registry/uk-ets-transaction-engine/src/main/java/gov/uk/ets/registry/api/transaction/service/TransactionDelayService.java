package gov.uk.ets.registry.api.transaction.service;

import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import gov.uk.ets.registry.api.transaction.repository.HolidayRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for calculating transaction delay
 */
@Service
@RequiredArgsConstructor
public class TransactionDelayService {

    private static final int ONE_DAY = 1;

    /**
     * Repository for UK bank holidays.
     */
    private final HolidayRepository holidayRepository;

    /**
     * Transaction Delay Properties
     */
    private final TransactionDelayProperties transactionDelayProperties;

    /**
     * Service for accounts.
     */
    private final TransactionPersistenceService transactionPersistenceService;

    /**
     * Calculates  the start clock.
     *
     * @param approvalDate The moment when the transaction or TAL was approved.
     * @param bankHolidays The bank holidays.
     * @return the start clock.
     */
    public LocalDateTime calculateStartClock(LocalDateTime approvalDate, LocalDate[] bankHolidays) {
        return shiftTime(approvalDate, bankHolidays, false);
    }

    /**
     * Calculates the delay for executing a transaction.
     *
     * @param acquiringAccountTrusted Whether the acquiring account is trusted or not.
     * @return the execution time.
     */
    public LocalDateTime calculateTransactionDelay(boolean acquiringAccountTrusted) {
        return calculateDelay(LocalDateTime.now(), false, acquiringAccountTrusted);
    }

    /**
     * Calculates the delay for adding an account to the trusted list (TAL).
     *
     * @return the execution time.
     */
    public LocalDateTime calculateTrustedAccountListDelay() {
        return calculateDelay(LocalDateTime.now(ZoneId.systemDefault()), true, false);
    }

    /**
     * Determines whether the provide transaction is valid for a delay.
     *
     * @param transaction               The transaction.
     * @return false/true
     */
    public boolean isTransactionValidForDelay(Transaction transaction) {
        if (Boolean.TRUE.equals(transactionDelayProperties.getDisableDelays())) {
            return false;
        }
        boolean isTransfer = transaction.getType().getDelayApplies();
        AccountSummary account =
            transactionPersistenceService.getAccount(transaction.getAcquiringAccount().getAccountFullIdentifier());
        boolean isAcquiringAccountGovernment = account != null && account.getType().isGovernmentAccount();
        boolean isInbound = Constants.isInboundTransaction(transaction);
        return !isAcquiringAccountGovernment && isTransfer && !isInbound;
    }

    /**
     * Calculates  the stop clock.
     *
     * @param startClock The Transaction proposal start clock.
     * @param isTAL      Whether the calculation is about a transaction or a TAL.
     * @return the stop Clock in LocalDateTime
     */
    public LocalDateTime calculateStopClock(LocalDateTime startClock, boolean isTAL, boolean acquiringAccountTrusted,
                                            LocalDate[] bankHolidays) {
        int delayInMinutes = getDelayMinutes(isTAL, acquiringAccountTrusted);
        int quotientDelay = delayInMinutes / 1440;
        int remainderDelayInMinutes = delayInMinutes % 1440;
        return calculateAddQuotient(startClock, bankHolidays, quotientDelay).plusMinutes(remainderDelayInMinutes);
    }

    /**
     * Calculates  the execution time.
     *
     * @param stopClock    The Transaction proposal stop clock.
     * @param bankHolidays An array with the bankHolidays to exclude.
     * @return the execution Time in LocalDateTime
     */
    public LocalDateTime calculateExecutionTime(LocalDateTime stopClock, LocalDate[] bankHolidays) {
        return shiftTime(stopClock, bankHolidays, true);
    }

    /**
     * Checks whether provided date is a working day, i.e. it does not belong to a weekend nor to bank holidays.
     *
     * @param time     The provided date.
     * @param holidays The bank holidays.
     * @return false/true
     */
    private static boolean isWorkingDay(LocalDate time, LocalDate[] holidays) {
        boolean isWeekend = time.getDayOfWeek().getValue() >= 6;
        boolean isHoliday = Arrays.asList(holidays).contains(time);
        return !isWeekend && !isHoliday;
    }

    /**
     * Calculates  the start clock, stop clock and execution Time given a transfer transaction proposal Date.
     *
     * @param transactionProposalDate The Transaction proposal date.
     * @param isTrustedAccountList    Whether the delay involves a TAL.
     * @return the execution Time in LocalDateTime
     */
    private LocalDateTime calculateDelay(LocalDateTime transactionProposalDate, boolean isTrustedAccountList,
                                         boolean acquiringAccountTrusted) {
        LocalDate[] bankHolidays = holidayRepository
            .findAll()
            .stream()
            .filter(Objects::nonNull)
            .map(holiday -> new Date(holiday.getDate().getTime())
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate())
            .toList().toArray(LocalDate[]::new);
        LocalDateTime startClock = calculateStartClock(transactionProposalDate, bankHolidays);
        LocalDateTime stopClock =
            calculateStopClock(startClock, isTrustedAccountList, acquiringAccountTrusted, bankHolidays);
        return calculateExecutionTime(stopClock, bankHolidays);
    }

    /**
     * Retrieves the applicable delay in minutes.
     *
     * @param isTAL                   Whether the calculation is about a TAL addition.
     * @param acquiringAccountTrusted Whether the acquiring account of the transaction is trusted.
     * @return the delay in minutes.
     */
    private int getDelayMinutes(boolean isTAL, boolean acquiringAccountTrusted) {
        int result = 0;

        if (isTAL) {
            result = transactionDelayProperties.getTrustedAccountListDelay();

        } else if (!acquiringAccountTrusted) {
            result = transactionDelayProperties.getTransactionDelay();
        }

        return result;
    }

    /**
     * Shifts the provided date, according to the delay algorithm.
     *
     * @param date         The input date.
     * @param bankHolidays The bank holidays.
     * @return the start clock.
     */
    private LocalDateTime shiftTime(LocalDateTime date, LocalDate[] bankHolidays,
                                    boolean respectTimeDuringNonWorkingDays) {
        LocalTime start =
            convertUkLondonTimeToDefault(date.toLocalDate(), transactionDelayProperties.getWorkingHoursStart());
        LocalTime end =
            convertUkLondonTimeToDefault(date.toLocalDate(), transactionDelayProperties.getWorkingHoursEnd());

        LocalDateTime result = date;
        LocalTime time = result.toLocalTime();

        if (time.isBefore(start)) {
            result = LocalDateTime.of(result.toLocalDate(), start);

        } else if (time.isAfter(end)) {
            LocalDate newDate = add(result.toLocalDate(), bankHolidays);
            result = LocalDateTime.of(newDate, start);
        }


        if (!isWorkingDay(result.toLocalDate(), bankHolidays)) {
            // Week-end or bank holiday
            LocalDate newDate = add(result.toLocalDate(), bankHolidays);
            result = respectTimeDuringNonWorkingDays ? LocalDateTime.of(newDate, result.toLocalTime()) :
                LocalDateTime.of(newDate, start);
        }

        return result;
    }

    /**
     * Returns a date after adding working dates taking in account to exclude bankHolidays.
     *
     * @param date         The date to increment.
     * @param bankHolidays An array with the bankHolidays to exclude.
     * @return a LocalDate after processing.
     */
    private LocalDate add(LocalDate date, LocalDate[] bankHolidays) {
        LocalDate result = date;
        int addedDays = 0;
        while (addedDays < TransactionDelayService.ONE_DAY) {
            result = result.plusDays(ONE_DAY);
            if (isWorkingDay(result, bankHolidays)) {
                ++addedDays;
            }
        }
        return result;
    }


    /**
     * Converts the London datetime given, to its corresponding time in UTC.
     * For example, the input 2023-07-03T10:00:00 (summer) should be converted to UTC as 09:00:00
     * and the input 2023-12-03T10:00:00 (winter) should be converted to UTC as 10:00:00
     */
    private LocalTime convertUkLondonTimeToDefault(LocalDate date, String hour) {
        return ZonedDateTime.of(date, LocalTime.parse(hour), ZoneId.of("Europe/London"))
                .withZoneSameInstant(ZoneId.systemDefault())
                .toLocalTime();
    }

    private LocalDateTime calculateAddQuotient(LocalDateTime startClockDate, LocalDate[] bankHolidays,
                                               int quotientDelay) {
        LocalDate resultClockDate = startClockDate.toLocalDate();
        LocalTime resultClockTime = startClockDate.toLocalTime();
        for (int i = 0; i < quotientDelay; i++) {
            resultClockDate = add(resultClockDate, bankHolidays);
        }
        return resultClockDate.atTime(resultClockTime);
    }

}
