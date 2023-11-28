package gov.uk.ets.registry.api.transaction.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.transaction.domain.AccountBasicInfo;
import gov.uk.ets.registry.api.transaction.domain.Holiday;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.repository.HolidayRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@Log4j2
class TransactionDelayServiceTest {

    @InjectMocks
    private TransactionDelayService transactionDelayService;

    @Mock
    private TransactionDelayProperties transactionDelayProperties;

    @Mock
    private HolidayRepository holidayRepository;

    @Mock
    private TransactionPersistenceService transactionPersistenceService;

    @Mock
    private TransactionAccountService transactionAccountService;

    private final LocalDate[] holidays = new LocalDate[] {
        day("16/03/2020"),
        day("04/04/2020"),
        day("04/05/2020"),
        day("05/05/2020"),
        day("06/05/2020"),
        day("07/05/2020"),
        day("08/05/2020"),
        day("11/05/2020"),
        day("20/07/2020"),
        day("05/09/2020"),
        day("01/08/2020"),
        day("01/01/2021"),
        day("02/04/2021"),
        day("05/04/2021"),
        day("03/05/2021"),
        day("31/05/2021"),
        day("15/06/2021"),
        day("16/06/2021"),
        day("17/06/2021"),
        day("30/08/2021"),
        day("27/12/2021"),
        day("28/12/2021")
    };

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);

        List<Holiday> bankHolidays = new ArrayList<>();
        for (LocalDate date : holidays) {
            Holiday holiday = new Holiday();
            holiday.setDate(java.sql.Date.valueOf(date));
            bankHolidays.add(holiday);
        }

        when(holidayRepository.findAll()).thenReturn(bankHolidays);
        when(transactionDelayProperties.getTransactionDelay()).thenReturn(1440);
        when(transactionDelayProperties.getTrustedAccountListDelay()).thenReturn(2880);
        when(transactionDelayProperties.getIgnoreHolidaysAndWeekends()).thenReturn(false);
    }


    @ParameterizedTest
    @CsvSource(value = {
        // Approval date     Start clock         Execution (7m)      Execution (2h)      Execution (24h)
        // Monday 15 March 2021 - Working day
        "15/03/2021 10:00:00,15/03/2021 10:00:00,15/03/2021 10:07:00,15/03/2021 12:00:00,16/03/2021 10:00:00,18/03/2021 10:00:00",
        "15/03/2021 10:00:01,15/03/2021 10:00:01,15/03/2021 10:07:01,15/03/2021 12:00:01,16/03/2021 10:00:01,18/03/2021 10:00:01",
        "15/03/2021 11:00:44,15/03/2021 11:00:44,15/03/2021 11:07:44,15/03/2021 13:00:44,16/03/2021 11:00:44,18/03/2021 11:00:44",
        "15/03/2021 15:59:59,15/03/2021 15:59:59,16/03/2021 10:00:00,16/03/2021 10:00:00,16/03/2021 15:59:59,18/03/2021 15:59:59",
        "15/03/2021 16:00:00,15/03/2021 16:00:00,16/03/2021 10:00:00,16/03/2021 10:00:00,16/03/2021 16:00:00,18/03/2021 16:00:00",

        "15/03/2021 00:00:00,15/03/2021 10:00:00,15/03/2021 10:07:00,15/03/2021 12:00:00,16/03/2021 10:00:00,18/03/2021 10:00:00",
        "15/03/2021 00:00:01,15/03/2021 10:00:00,15/03/2021 10:07:00,15/03/2021 12:00:00,16/03/2021 10:00:00,18/03/2021 10:00:00",
        "15/03/2021 08:34:42,15/03/2021 10:00:00,15/03/2021 10:07:00,15/03/2021 12:00:00,16/03/2021 10:00:00,18/03/2021 10:00:00",
        "15/03/2021 16:00:01,16/03/2021 10:00:00,16/03/2021 10:07:00,16/03/2021 12:00:00,17/03/2021 10:00:00,19/03/2021 10:00:00",
        "15/03/2021 17:42:48,16/03/2021 10:00:00,16/03/2021 10:07:00,16/03/2021 12:00:00,17/03/2021 10:00:00,19/03/2021 10:00:00",
        "15/03/2021 23:59:59,16/03/2021 10:00:00,16/03/2021 10:07:00,16/03/2021 12:00:00,17/03/2021 10:00:00,19/03/2021 10:00:00",

        // Friday 19 March 2021 - Working day
        "19/03/2021 10:00:00,19/03/2021 10:00:00,19/03/2021 10:07:00,19/03/2021 12:00:00,22/03/2021 10:00:00,24/03/2021 10:00:00",
        "19/03/2021 10:00:01,19/03/2021 10:00:01,19/03/2021 10:07:01,19/03/2021 12:00:01,22/03/2021 10:00:01,24/03/2021 10:00:01",
        "19/03/2021 11:00:44,19/03/2021 11:00:44,19/03/2021 11:07:44,19/03/2021 13:00:44,22/03/2021 11:00:44,24/03/2021 11:00:44",
        "19/03/2021 15:59:59,19/03/2021 15:59:59,22/03/2021 10:00:00,22/03/2021 10:00:00,22/03/2021 15:59:59,24/03/2021 15:59:59",
        "19/03/2021 16:00:00,19/03/2021 16:00:00,22/03/2021 10:00:00,22/03/2021 10:00:00,22/03/2021 16:00:00,24/03/2021 16:00:00",

        "19/03/2021 00:00:00,19/03/2021 10:00:00,19/03/2021 10:07:00,19/03/2021 12:00:00,22/03/2021 10:00:00,24/03/2021 10:00:00",
        "19/03/2021 00:00:01,19/03/2021 10:00:00,19/03/2021 10:07:00,19/03/2021 12:00:00,22/03/2021 10:00:00,24/03/2021 10:00:00",
        "19/03/2021 08:34:42,19/03/2021 10:00:00,19/03/2021 10:07:00,19/03/2021 12:00:00,22/03/2021 10:00:00,24/03/2021 10:00:00",
        "19/03/2021 16:00:01,22/03/2021 10:00:00,22/03/2021 10:07:00,22/03/2021 12:00:00,23/03/2021 10:00:00,25/03/2021 10:00:00",
        "19/03/2021 17:42:48,22/03/2021 10:00:00,22/03/2021 10:07:00,22/03/2021 12:00:00,23/03/2021 10:00:00,25/03/2021 10:00:00",
        "19/03/2021 23:59:59,22/03/2021 10:00:00,22/03/2021 10:07:00,22/03/2021 12:00:00,23/03/2021 10:00:00,25/03/2021 10:00:00",

        // Saturday 20 March 2021 - Weekend
        "20/03/2021 10:00:00,22/03/2021 10:00:00,22/03/2021 10:07:00,22/03/2021 12:00:00,23/03/2021 10:00:00,25/03/2021 10:00:00",
        "20/03/2021 10:00:01,22/03/2021 10:00:00,22/03/2021 10:07:00,22/03/2021 12:00:00,23/03/2021 10:00:00,25/03/2021 10:00:00",
        "20/03/2021 11:00:44,22/03/2021 10:00:00,22/03/2021 10:07:00,22/03/2021 12:00:00,23/03/2021 10:00:00,25/03/2021 10:00:00",
        "20/03/2021 15:59:59,22/03/2021 10:00:00,22/03/2021 10:07:00,22/03/2021 12:00:00,23/03/2021 10:00:00,25/03/2021 10:00:00",
        "20/03/2021 16:00:00,22/03/2021 10:00:00,22/03/2021 10:07:00,22/03/2021 12:00:00,23/03/2021 10:00:00,25/03/2021 10:00:00",

        "20/03/2021 00:00:00,22/03/2021 10:00:00,22/03/2021 10:07:00,22/03/2021 12:00:00,23/03/2021 10:00:00,25/03/2021 10:00:00",
        "20/03/2021 00:00:01,22/03/2021 10:00:00,22/03/2021 10:07:00,22/03/2021 12:00:00,23/03/2021 10:00:00,25/03/2021 10:00:00",
        "20/03/2021 08:34:42,22/03/2021 10:00:00,22/03/2021 10:07:00,22/03/2021 12:00:00,23/03/2021 10:00:00,25/03/2021 10:00:00",
        "20/03/2021 16:00:01,22/03/2021 10:00:00,22/03/2021 10:07:00,22/03/2021 12:00:00,23/03/2021 10:00:00,25/03/2021 10:00:00",
        "20/03/2021 17:42:48,22/03/2021 10:00:00,22/03/2021 10:07:00,22/03/2021 12:00:00,23/03/2021 10:00:00,25/03/2021 10:00:00",
        "20/03/2021 23:59:59,22/03/2021 10:00:00,22/03/2021 10:07:00,22/03/2021 12:00:00,23/03/2021 10:00:00,25/03/2021 10:00:00",

        // Sunday 21 March 2021 - Weekend
        "21/03/2021 10:00:00,22/03/2021 10:00:00,22/03/2021 10:07:00,22/03/2021 12:00:00,23/03/2021 10:00:00,25/03/2021 10:00:00",
        "21/03/2021 10:00:01,22/03/2021 10:00:00,22/03/2021 10:07:00,22/03/2021 12:00:00,23/03/2021 10:00:00,25/03/2021 10:00:00",
        "21/03/2021 11:00:44,22/03/2021 10:00:00,22/03/2021 10:07:00,22/03/2021 12:00:00,23/03/2021 10:00:00,25/03/2021 10:00:00",
        "21/03/2021 15:59:59,22/03/2021 10:00:00,22/03/2021 10:07:00,22/03/2021 12:00:00,23/03/2021 10:00:00,25/03/2021 10:00:00",
        "21/03/2021 16:00:00,22/03/2021 10:00:00,22/03/2021 10:07:00,22/03/2021 12:00:00,23/03/2021 10:00:00,25/03/2021 10:00:00",

        "21/03/2021 00:00:00,22/03/2021 10:00:00,22/03/2021 10:07:00,22/03/2021 12:00:00,23/03/2021 10:00:00,25/03/2021 10:00:00",
        "21/03/2021 00:00:01,22/03/2021 10:00:00,22/03/2021 10:07:00,22/03/2021 12:00:00,23/03/2021 10:00:00,25/03/2021 10:00:00",
        "21/03/2021 08:34:42,22/03/2021 10:00:00,22/03/2021 10:07:00,22/03/2021 12:00:00,23/03/2021 10:00:00,25/03/2021 10:00:00",
        "21/03/2021 16:00:01,22/03/2021 10:00:00,22/03/2021 10:07:00,22/03/2021 12:00:00,23/03/2021 10:00:00,25/03/2021 10:00:00",
        "21/03/2021 17:42:48,22/03/2021 10:00:00,22/03/2021 10:07:00,22/03/2021 12:00:00,23/03/2021 10:00:00,25/03/2021 10:00:00",
        "21/03/2021 23:59:59,22/03/2021 10:00:00,22/03/2021 10:07:00,22/03/2021 12:00:00,23/03/2021 10:00:00,25/03/2021 10:00:00",

        // Friday 30 April 2021 - Working day
        "30/04/2021 09:50:00,30/04/2021 10:00:00,30/04/2021 10:07:00,30/04/2021 12:00:00,04/05/2021 10:00:00,06/05/2021 10:00:00",
        "30/04/2021 11:00:00,30/04/2021 11:00:00,30/04/2021 11:07:00,30/04/2021 13:00:00,04/05/2021 11:00:00,06/05/2021 11:00:00",
        "30/04/2021 15:00:00,30/04/2021 15:00:00,30/04/2021 15:07:00,04/05/2021 10:00:00,04/05/2021 15:00:00,06/05/2021 15:00:00",
        "29/04/2021 15:59:00,29/04/2021 15:59:00,30/04/2021 10:00:00,30/04/2021 10:00:00,30/04/2021 15:59:00,05/05/2021 15:59:00",
        "29/04/2021 16:01:00,30/04/2021 10:00:00,30/04/2021 10:07:00,30/04/2021 12:00:00,04/05/2021 10:00:00,06/05/2021 10:00:00",
        "30/04/2021 16:01:00,04/05/2021 10:00:00,04/05/2021 10:07:00,04/05/2021 12:00:00,05/05/2021 10:00:00,07/05/2021 10:00:00",
        "01/05/2021 12:00:00,04/05/2021 10:00:00,04/05/2021 10:07:00,04/05/2021 12:00:00,05/05/2021 10:00:00,07/05/2021 10:00:00",

        // Monday 03 May 2021 - Bank holiday
        "03/05/2021 10:00:00,04/05/2021 10:00:00,04/05/2021 10:07:00,04/05/2021 12:00:00,05/05/2021 10:00:00,07/05/2021 10:00:00",
        "03/05/2021 10:00:01,04/05/2021 10:00:00,04/05/2021 10:07:00,04/05/2021 12:00:00,05/05/2021 10:00:00,07/05/2021 10:00:00",
        "03/05/2021 11:00:44,04/05/2021 10:00:00,04/05/2021 10:07:00,04/05/2021 12:00:00,05/05/2021 10:00:00,07/05/2021 10:00:00",
        "03/05/2021 15:59:59,04/05/2021 10:00:00,04/05/2021 10:07:00,04/05/2021 12:00:00,05/05/2021 10:00:00,07/05/2021 10:00:00",
        "03/05/2021 16:00:00,04/05/2021 10:00:00,04/05/2021 10:07:00,04/05/2021 12:00:00,05/05/2021 10:00:00,07/05/2021 10:00:00",

        "03/05/2021 00:00:00,04/05/2021 10:00:00,04/05/2021 10:07:00,04/05/2021 12:00:00,05/05/2021 10:00:00,07/05/2021 10:00:00",
        "03/05/2021 00:00:01,04/05/2021 10:00:00,04/05/2021 10:07:00,04/05/2021 12:00:00,05/05/2021 10:00:00,07/05/2021 10:00:00",
        "03/05/2021 08:34:42,04/05/2021 10:00:00,04/05/2021 10:07:00,04/05/2021 12:00:00,05/05/2021 10:00:00,07/05/2021 10:00:00",
        "03/05/2021 16:00:01,04/05/2021 10:00:00,04/05/2021 10:07:00,04/05/2021 12:00:00,05/05/2021 10:00:00,07/05/2021 10:00:00",
        "03/05/2021 17:42:48,04/05/2021 10:00:00,04/05/2021 10:07:00,04/05/2021 12:00:00,05/05/2021 10:00:00,07/05/2021 10:00:00",
        "03/05/2021 23:59:59,04/05/2021 10:00:00,04/05/2021 10:07:00,04/05/2021 12:00:00,05/05/2021 10:00:00,07/05/2021 10:00:00",

        // Monday 14 June 2021 - Working day (3 bank holidays follow)
        // Approval date     Start clock         Execution (7m)      Execution (2h)      Execution (24h)
        "14/06/2021 10:00:00,14/06/2021 10:00:00,14/06/2021 10:07:00,14/06/2021 12:00:00,18/06/2021 10:00:00,22/06/2021 10:00:00",
        "14/06/2021 10:00:01,14/06/2021 10:00:01,14/06/2021 10:07:01,14/06/2021 12:00:01,18/06/2021 10:00:01,22/06/2021 10:00:01",
        "14/06/2021 11:00:44,14/06/2021 11:00:44,14/06/2021 11:07:44,14/06/2021 13:00:44,18/06/2021 11:00:44,22/06/2021 11:00:44",
        "14/06/2021 15:59:59,14/06/2021 15:59:59,18/06/2021 10:00:00,18/06/2021 10:00:00,18/06/2021 15:59:59,22/06/2021 15:59:59",
        "14/06/2021 16:00:00,14/06/2021 16:00:00,18/06/2021 10:00:00,18/06/2021 10:00:00,18/06/2021 16:00:00,22/06/2021 16:00:00",

        "14/06/2021 00:00:00,14/06/2021 10:00:00,14/06/2021 10:07:00,14/06/2021 12:00:00,18/06/2021 10:00:00,22/06/2021 10:00:00",
        "14/06/2021 00:00:01,14/06/2021 10:00:00,14/06/2021 10:07:00,14/06/2021 12:00:00,18/06/2021 10:00:00,22/06/2021 10:00:00",
        "14/06/2021 08:34:42,14/06/2021 10:00:00,14/06/2021 10:07:00,14/06/2021 12:00:00,18/06/2021 10:00:00,22/06/2021 10:00:00",
        "14/06/2021 16:00:01,18/06/2021 10:00:00,18/06/2021 10:07:00,18/06/2021 12:00:00,21/06/2021 10:00:00,23/06/2021 10:00:00",
        "14/06/2021 17:42:48,18/06/2021 10:00:00,18/06/2021 10:07:00,18/06/2021 12:00:00,21/06/2021 10:00:00,23/06/2021 10:00:00",
        "14/06/2021 23:59:59,18/06/2021 10:00:00,18/06/2021 10:07:00,18/06/2021 12:00:00,21/06/2021 10:00:00,23/06/2021 10:00:00"

    })
    void calculateDelay(String approveDate, String startClock, String execution7mDelay, String executionDate2hDelay,
                        String executionDate24hDelay, String executionDate72hDelay) {

        convertDefaultToUkLondonTime(date(approveDate).toLocalDate());
        assertEquals(date(startClock), startClock(approveDate));

        // No additional delay
        assertEquals(date(startClock), stopClock(startClock, false, true));

        // Delay of 7 minutes
        LocalDateTime stopClock = date(startClock).plusMinutes(7);
        when(transactionDelayProperties.getTransactionDelay()).thenReturn(7);
        assertEquals(stopClock, stopClock(startClock, false, false));
        assertEquals(date(execution7mDelay), executionTime(date(stopClock)));

        // Delay of 2 hours
        stopClock = date(startClock).plusHours(2);
        when(transactionDelayProperties.getTransactionDelay()).thenReturn(2 * 60);
        assertEquals(stopClock, stopClock(startClock, false, false));
        assertEquals(date(executionDate2hDelay), executionTime(date(stopClock)));

        // Delay of 24 hours
        when(transactionDelayProperties.getTrustedAccountListDelay()).thenReturn(24 * 60);
        assertEquals(date(executionDate24hDelay), executionTime(date(stopClock(startClock, true, false))));

        // Delay of 72 hours
        when(transactionDelayProperties.getTrustedAccountListDelay()).thenReturn(72 * 60);
        assertEquals(date(executionDate72hDelay), executionTime(date(stopClock(startClock, true, false))));
    }

    @ParameterizedTest
    @CsvSource(value = {
        // Approval date     Start clock          Execution (7h)     Execution (10h)     Execution (12h)     Execution (32h)     Execution (34h)     Execution (36h)
        // Thursday 29/04/2021 - Working day
        "29/04/2021 16:01:00,30/04/2021 10:00:00,04/05/2021 10:00:00,04/05/2021 10:00:00,04/05/2021 10:00:00,05/05/2021 10:00:00,05/05/2021 10:00:00,05/05/2021 10:00:00",
        "29/04/2021 10:01:00,29/04/2021 10:01:00,30/04/2021 10:00:00,30/04/2021 10:00:00,30/04/2021 10:00:00,04/05/2021 10:00:00,04/05/2021 10:00:00,04/05/2021 10:00:00",
        "30/04/2021 06:32:00,30/04/2021 10:00:00,04/05/2021 10:00:00,04/05/2021 10:00:00,04/05/2021 10:00:00,05/05/2021 10:00:00,05/05/2021 10:00:00,05/05/2021 10:00:00",
        "30/04/2021 18:44:00,04/05/2021 10:00:00,05/05/2021 10:00:00,05/05/2021 10:00:00,05/05/2021 10:00:00,06/05/2021 10:00:00,06/05/2021 10:00:00,06/05/2021 10:00:00",
        // near Friday 15:50 start time
        "30/04/2021 15:50:00,30/04/2021 15:50:00,04/05/2021 10:00:00,04/05/2021 10:00:00,04/05/2021 10:00:00,05/05/2021 10:00:00,05/05/2021 10:00:00,05/05/2021 10:00:00",
    })
    void calculateDelayAdditionalScenarios(String approveDate, String startClock, String executionDate7h,
                                           String executionDate10h, String executionDate12h, String executionDate32h,
                                           String executionDate34h, String executionDate36h) {

        convertDefaultToUkLondonTime(date(approveDate).toLocalDate());
        assertEquals(date(startClock), startClock(approveDate));

        // Delay of 7 hours
        when(transactionDelayProperties.getTrustedAccountListDelay()).thenReturn(7 * 60);
        assertEquals(date(executionDate7h), executionTime(date(stopClock(startClock, true, false))));

        // Delay of 10 hours
        when(transactionDelayProperties.getTrustedAccountListDelay()).thenReturn(10 * 60);
        assertEquals(date(executionDate10h), executionTime(date(stopClock(startClock, true, false))));

        // Delay of 12 hours
        when(transactionDelayProperties.getTrustedAccountListDelay()).thenReturn(12 * 60);
        assertEquals(date(executionDate12h), executionTime(date(stopClock(startClock, true, false))));

        // Delay of 32 hours
        when(transactionDelayProperties.getTrustedAccountListDelay()).thenReturn(32 * 60);
        assertEquals(date(executionDate32h), executionTime(date(stopClock(startClock, true, false))));

        // Delay of 34 hours
        when(transactionDelayProperties.getTrustedAccountListDelay()).thenReturn(34 * 60);
        assertEquals(date(executionDate34h), executionTime(date(stopClock(startClock, true, false))));

        // Delay of 36 hours
        when(transactionDelayProperties.getTrustedAccountListDelay()).thenReturn(36 * 60);
        assertEquals(date(executionDate36h), executionTime(date(stopClock(startClock, true, false))));
    }

    private LocalDateTime startClock(String transactionProposalDate) {
        LocalDateTime date = date(transactionProposalDate);
        LocalDateTime startClock = transactionDelayService.calculateStartClock(date, holidays);
        log.info("Transaction proposal Date {} {}", date.getDayOfWeek(), date);
        log.info("Start Clock {} {}", startClock.getDayOfWeek(), startClock);
        return startClock;
    }

    private LocalDateTime stopClock(String startClock, boolean isTAL, boolean acquiringAccountTrusted) {
        LocalDateTime date = date(startClock);
        LocalDateTime stopClock =
            transactionDelayService.calculateStopClock(date, isTAL, acquiringAccountTrusted, holidays);
        log.info("Start Clock {} {}", date.getDayOfWeek(), date);
        log.info("Stop Clock {} {}", stopClock.getDayOfWeek(), stopClock);
        return stopClock;
    }

    private LocalDateTime executionTime(String stopClock) {
        LocalDateTime date = date(stopClock);
        LocalDateTime executionDateTime = transactionDelayService.calculateExecutionTime(date, holidays);
        log.info("Stop Clock {} {}", date.getDayOfWeek(), date);
        log.info("Execution Time {} {}", executionDateTime.getDayOfWeek(), executionDateTime);
        return executionDateTime;
    }

    @Test
    void testIfProposalValidForDelay() {

        convertDefaultToUkLondonTime(LocalDate.now());
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.ExternalTransfer);
        AccountBasicInfo acquiringAccount = new AccountBasicInfo();
        acquiringAccount.setAccountFullIdentifier("GB-100-12345-2-22");
        transaction.setAcquiringAccount(acquiringAccount);

        AccountBasicInfo transferringAccount = new AccountBasicInfo();
        transferringAccount.setAccountRegistryCode("GB");
        transferringAccount.setAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        transferringAccount.setAccountIdentifier(10000L);
        transaction.setTransferringAccount(transferringAccount);
        AccountSummary accountSummary = AccountSummary
            .parse(acquiringAccount.getAccountFullIdentifier(), RegistryAccountType.OPERATOR_HOLDING_ACCOUNT,
                AccountStatus.OPEN);
        assertNotNull(accountSummary);
        when(transactionPersistenceService.getAccount(acquiringAccount.getAccountFullIdentifier()))
            .thenReturn(accountSummary);

        when(transactionAccountService.isTrustedAccount(any(), any(), any())).thenReturn(false);
        assertTrue(transactionDelayService.isTransactionValidForDelay(transaction));
        when(transactionAccountService.isTrustedAccount(any(), any(), any())).thenReturn(true);
        assertTrue(transactionDelayService.isTransactionValidForDelay(transaction));

        // Initiator is an admin
        when(transactionAccountService.isTrustedAccount(any(), any(), any())).thenReturn(false);
        assertTrue(transactionDelayService.isTransactionValidForDelay(transaction));
        when(transactionAccountService.isTrustedAccount(any(), any(), any())).thenReturn(true);
        assertTrue(transactionDelayService.isTransactionValidForDelay(transaction));

        // Acquiring account is not a government account
        when(transactionAccountService.isTrustedAccount(any(), any(), any())).thenReturn(false);
        accountSummary.setType(AccountType.PARTY_HOLDING_ACCOUNT);
        assertFalse(transactionDelayService.isTransactionValidForDelay(transaction));
        when(transactionAccountService.isTrustedAccount(any(), any(), any())).thenReturn(true);
        assertFalse(transactionDelayService.isTransactionValidForDelay(transaction));

        transaction.setType(TransactionType.InternalTransfer);
        accountSummary.setType(AccountType.UK_AUCTION_ACCOUNT);
        when(transactionAccountService.isTrustedAccount(any(), any(), any())).thenReturn(false);
        assertFalse(transactionDelayService.isTransactionValidForDelay(transaction));
        when(transactionAccountService.isTrustedAccount(any(), any(), any())).thenReturn(true);
        assertFalse(transactionDelayService.isTransactionValidForDelay(transaction));

        // Transaction is not a transfer
        transaction.setType(TransactionType.CarryOver_AAU);
        accountSummary.setType(AccountType.PERSON_HOLDING_ACCOUNT);
        when(transactionAccountService.isTrustedAccount(any(), any(), any())).thenReturn(false);
        assertFalse(transactionDelayService.isTransactionValidForDelay(transaction));
        assertFalse(transactionDelayService.isTransactionValidForDelay(transaction));

        when(transactionAccountService.isTrustedAccount(any(), any(), any())).thenReturn(true);
        assertFalse(transactionDelayService.isTransactionValidForDelay(transaction));
        assertFalse(transactionDelayService.isTransactionValidForDelay(transaction));

        // No acquiring account
        when(transactionPersistenceService.getAccount(acquiringAccount.getAccountFullIdentifier())).thenReturn(null);
        when(transactionAccountService.isTrustedAccount(any(), any(), any())).thenReturn(false);
        assertFalse(transactionDelayService.isTransactionValidForDelay(transaction));
        assertFalse(transactionDelayService.isTransactionValidForDelay(transaction));

        when(transactionAccountService.isTrustedAccount(any(), any(), any())).thenReturn(true);
        assertFalse(transactionDelayService.isTransactionValidForDelay(transaction));
        assertFalse(transactionDelayService.isTransactionValidForDelay(transaction));


        assertNotNull(transactionDelayService.calculateTransactionDelay(false));
        assertNotNull(transactionDelayService.calculateTrustedAccountListDelay());


        transferringAccount = new AccountBasicInfo();
        transferringAccount.setAccountRegistryCode("JP");
        transferringAccount.setAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        transferringAccount.setAccountIdentifier(700L);
        transaction.setTransferringAccount(transferringAccount);
        transaction.setType(TransactionType.ExternalTransfer);

        // Transfer outside registry
        when(transactionAccountService.isTrustedAccount(any(), any(), any())).thenReturn(false);
        assertFalse(transactionDelayService.isTransactionValidForDelay(transaction));
        assertFalse(transactionDelayService.isTransactionValidForDelay(transaction));

        when(transactionAccountService.isTrustedAccount(any(), any(), any())).thenReturn(true);
        assertFalse(transactionDelayService.isTransactionValidForDelay(transaction));
        assertFalse(transactionDelayService.isTransactionValidForDelay(transaction));

    }

    @Test
    void testTransferOfAllowances() {
        Transaction transaction = new Transaction();

        AccountBasicInfo acquiringAccount = new AccountBasicInfo();
        acquiringAccount.setAccountFullIdentifier("UK-100-12345-2-22");
        transaction.setAcquiringAccount(acquiringAccount);

        AccountSummary accountSummary = AccountSummary.parse(acquiringAccount.getAccountFullIdentifier(),
            RegistryAccountType.OPERATOR_HOLDING_ACCOUNT, AccountStatus.OPEN);
        when(transactionPersistenceService.getAccount(acquiringAccount.getAccountFullIdentifier()))
            .thenReturn(accountSummary);

        AccountBasicInfo transferringAccount = new AccountBasicInfo();
        transferringAccount.setAccountRegistryCode("UK");
        transferringAccount.setAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        transferringAccount.setAccountIdentifier(10200L);
        transaction.setTransferringAccount(transferringAccount);

        // Transfer of allowances
        transaction.setType(TransactionType.TransferAllowances);

        when(transactionAccountService.isTrustedAccount(any(), any(), any())).thenReturn(false);
        assertTrue(transactionDelayService.isTransactionValidForDelay(transaction));
        assertTrue(transactionDelayService.isTransactionValidForDelay(transaction));

        when(transactionAccountService.isTrustedAccount(any(), any(), any())).thenReturn(true);
        assertTrue(transactionDelayService.isTransactionValidForDelay(transaction));
        assertTrue(transactionDelayService.isTransactionValidForDelay(transaction));
    }

    @Test
    void testConfigurationPropertyForDelays() {
        Transaction transaction = new Transaction();
        AccountBasicInfo acquiringAccount = new AccountBasicInfo();
        acquiringAccount.setAccountFullIdentifier("GB-100-12345-2-22");
        transaction.setAcquiringAccount(acquiringAccount);
        AccountSummary accountSummary = AccountSummary.parse(acquiringAccount.getAccountFullIdentifier(),
            RegistryAccountType.NONE, AccountStatus.OPEN);
        assertNotNull(accountSummary);
        when(transactionPersistenceService.getAccount(acquiringAccount.getAccountFullIdentifier()))
            .thenReturn(accountSummary);

        // disable delays
        when(transactionDelayProperties.getDisableDelays()).thenReturn(true);
        for (TransactionType type : TransactionType.values()) {
            for (AccountType accountType : AccountType.values()) {
                transaction.setType(type);
                accountSummary.setType(accountType);
                when(transactionAccountService.isTrustedAccount(any(), any(), any())).thenReturn(false);
                assertFalse(transactionDelayService.isTransactionValidForDelay(transaction));
                assertFalse(transactionDelayService.isTransactionValidForDelay(transaction));

                when(transactionAccountService.isTrustedAccount(any(), any(), any())).thenReturn(true);
                assertFalse(transactionDelayService.isTransactionValidForDelay(transaction));
                assertFalse(transactionDelayService.isTransactionValidForDelay(transaction));
            }
        }
    }

    private void convertDefaultToUkLondonTime(LocalDate localDate) {

        ZonedDateTime utcInputStart = ZonedDateTime.of(localDate, LocalTime.parse("10:00"), ZoneId.systemDefault())
                .withZoneSameInstant(ZoneId.of("Europe/London"));

        ZonedDateTime utcInputEnd = ZonedDateTime.of(localDate, LocalTime.parse("16:00"), ZoneId.systemDefault())
                .withZoneSameInstant(ZoneId.of("Europe/London"));

        when(transactionDelayProperties.getWorkingHoursStart())
                .thenReturn(utcInputStart.toLocalTime().toString());
        when(transactionDelayProperties.getWorkingHoursEnd())
            .thenReturn(utcInputEnd.toLocalTime().toString());
    }

    private LocalDateTime date(String input) {
        return LocalDateTime.parse(input, FORMATTER);
    }

    private String date(LocalDateTime input) {
        return FORMATTER.format(input);
    }

    private LocalDate day(String input) {
        return LocalDate.parse(input, DAY_FORMATTER);
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

}
