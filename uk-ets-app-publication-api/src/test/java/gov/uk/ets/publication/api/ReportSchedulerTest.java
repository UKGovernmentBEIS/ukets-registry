package gov.uk.ets.publication.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.uk.ets.publication.api.domain.PublicationSchedule;
import gov.uk.ets.publication.api.domain.ReportFile;
import gov.uk.ets.publication.api.domain.Section;
import gov.uk.ets.publication.api.file.upload.repository.ReportFilesRepository;
import gov.uk.ets.publication.api.model.DisplayType;
import gov.uk.ets.publication.api.model.PublicationFrequency;
import gov.uk.ets.publication.api.model.SectionType;
import gov.uk.ets.publication.api.repository.PublicationScheduleRepository;
import gov.uk.ets.publication.api.repository.SectionRepository;
import gov.uk.ets.publication.api.scheduler.ReportScheduler;
import gov.uk.ets.publication.api.service.ReportGenerationService;
import gov.uk.ets.reports.model.ReportQueryInfo;
import gov.uk.ets.reports.model.ReportType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import net.javacrumbs.shedlock.core.LockAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ReportSchedulerTest {

    private static final String TEST_TITLE_1 = "Test Title 1";
    private static final String TEST_SUMMARY_1 = "Test Summary 1";
    private static final String TEST_TITLE_2 = "Test Title 2";
    private static final String TEST_SUMMARY_2 = "Test Summary 2";
    
    @Mock
    private SectionRepository sectionRepository;
    
    @Mock
    private ReportFilesRepository reportFilesRepository;
    
    @Mock
    private PublicationScheduleRepository scheduleRepository;

    @Mock
    private ReportGenerationService reportGenerationService;

    private ReportScheduler reportScheduler;
    
    @BeforeEach
    void setUp() {
        LockAssert.TestHelper.makeAllAssertsPass(true);
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    void noReportsEligibleForGeneration_startDateIsLater() {
        reportScheduler = new ReportScheduler(
                reportGenerationService, sectionRepository, reportFilesRepository, scheduleRepository);
        when(sectionRepository.findAll()).thenReturn(createSections(LocalDateTime.now().plusYears(1), false));
        reportScheduler.execute();
        verify(reportGenerationService, times(0)).requestReport(any(), any(), any(), any());
        verify(scheduleRepository, times(0)).save(any());
    }
    
    @Test
    void noReportsEligibleForGeneration_timeToTakeNextReportIsLater() {
        reportScheduler = new ReportScheduler(
                reportGenerationService, sectionRepository, reportFilesRepository, scheduleRepository);
        when(sectionRepository.findAll()).thenReturn(createSections(LocalDateTime.now().minusHours(10), false));
        reportScheduler.execute();
        verify(reportGenerationService, times(0)).requestReport(any(), any(), any(), any());
        verify(scheduleRepository, times(0)).save(any());
    }
    
    @Test
    void shouldRequestOneReportGeneration() {
        ReportFile file = new ReportFile();
        file.setId(10L);
        reportScheduler = new ReportScheduler(
                reportGenerationService, sectionRepository, reportFilesRepository, scheduleRepository);
        when(sectionRepository.findAll()).thenReturn(createSections(LocalDateTime.now(), false));
        when(reportFilesRepository.save(any())).thenReturn(file);
        when(scheduleRepository.findById(any())).thenReturn(Optional.of(new PublicationSchedule()));
        reportScheduler.execute();
        
        ArgumentCaptor<ReportType> argument1 = ArgumentCaptor.forClass(ReportType.class);
        ArgumentCaptor<Long> argument2 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<LocalDateTime> argument3 = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<ReportQueryInfo> argument4 = ArgumentCaptor.forClass(ReportQueryInfo.class);
        verify(reportGenerationService, times(1)).requestReport(
                argument1.capture(), argument2.capture(), argument3.capture(), argument4.capture());
        assertEquals(ReportType.R0013, argument1.getValue());
        assertEquals(10L, argument2.getValue());
        assertEquals((long) LocalDateTime.now().getYear(), argument4.getValue().getYear());
        
        verify(scheduleRepository, times(0)).save(any());
    }
    
    @Test
    void shouldRequestOneReportGenerationAndUpdateNextReportTime() {
        LocalDateTime now = LocalDateTime.now();
        ReportFile file = new ReportFile();
        file.setId(10L);
        reportScheduler = new ReportScheduler(
                reportGenerationService, sectionRepository, reportFilesRepository, scheduleRepository);
        when(sectionRepository.findAll()).thenReturn(createSections(now.minusDays(1), false));
        when(reportFilesRepository.save(any())).thenReturn(file);
        when(scheduleRepository.findById(any())).thenReturn(Optional.of(new PublicationSchedule()));
        reportScheduler.execute();
        
        ArgumentCaptor<ReportType> argument1 = ArgumentCaptor.forClass(ReportType.class);
        ArgumentCaptor<Long> argument2 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<LocalDateTime> argument3 = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<ReportQueryInfo> argument4 = ArgumentCaptor.forClass(ReportQueryInfo.class);
        verify(reportGenerationService, times(1)).requestReport(
                argument1.capture(), argument2.capture(), argument3.capture(), argument4.capture());
        assertEquals(ReportType.R0013, argument1.getValue());
        assertEquals(10L, argument2.getValue());
        assertEquals((long) LocalDateTime.now().getYear(), argument4.getValue().getYear());
               
        ArgumentCaptor<PublicationSchedule> argument5 = ArgumentCaptor.forClass(PublicationSchedule.class);
        verify(scheduleRepository, times(1)).save(argument5.capture());
        assertEquals(now.plusDays(1), argument5.getValue().getNextReportDate());
    }
    
    @Test
    void shouldRequestManyReportsGenerationAndUpdateNextReportTime() {
        LocalDateTime now = LocalDateTime.now();
        ReportFile file = new ReportFile();
        file.setId(10L);
        reportScheduler = new ReportScheduler(
                reportGenerationService, sectionRepository, reportFilesRepository, scheduleRepository);
        when(sectionRepository.findAll()).thenReturn(createSections(now.minusYears(1), true));
        when(reportFilesRepository.save(any())).thenReturn(file);
        when(scheduleRepository.findById(any())).thenReturn(Optional.of(new PublicationSchedule()));
        reportScheduler.execute();
        
        ArgumentCaptor<ReportType> argument1 = ArgumentCaptor.forClass(ReportType.class);
        ArgumentCaptor<Long> argument2 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<LocalDateTime> argument3 = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<ReportQueryInfo> argument4 = ArgumentCaptor.forClass(ReportQueryInfo.class);
        int numberOfReports = now.getYear() - 2021 + 1;
        verify(reportGenerationService, times(numberOfReports)).requestReport(
                argument1.capture(), argument2.capture(), argument3.capture(), argument4.capture());
        assertEquals(ReportType.R0014, argument1.getValue());
        assertEquals(10L, argument2.getValue());
               
        ArgumentCaptor<PublicationSchedule> argument5 = ArgumentCaptor.forClass(PublicationSchedule.class);
        verify(scheduleRepository, times(2)).save(argument5.capture());
        assertEquals(now.plusDays(1), argument5.getValue().getNextReportDate());
    }
  
    private List<Section> createSections(LocalDateTime date, boolean enableSecondSchedule) {
        Section etsSection1 = new Section();
        etsSection1.setId(1L);
        etsSection1.setSectionType(SectionType.ETS);
        etsSection1.setDisplayOrder(1);
        etsSection1.setTitle(TEST_TITLE_1);
        etsSection1.setSummary(TEST_SUMMARY_1);
        etsSection1.setDisplayType(DisplayType.ONE_FILE);
        etsSection1.setReportType(ReportType.R0013);

        Section etsSection2 = new Section();
        etsSection2.setId(2L);
        etsSection2.setSectionType(SectionType.ETS);
        etsSection2.setDisplayOrder(2);
        etsSection2.setTitle(TEST_TITLE_2);
        etsSection2.setSummary(TEST_SUMMARY_2);
        etsSection2.setDisplayType(DisplayType.ONE_FILE_PER_YEAR);
        etsSection2.setReportType(ReportType.R0014);
        
        PublicationSchedule schedule1 = new PublicationSchedule();
        schedule1.setStartDate(date);
        schedule1.setPublicationFrequency(PublicationFrequency.DAILY);
        schedule1.setGenerationDate(LocalDateTime.now());
        schedule1.setNextReportDate(date.plusDays(1));
        etsSection1.setPublicationSchedule(schedule1);
        
        PublicationSchedule schedule2 = new PublicationSchedule();
        schedule2.setPublicationFrequency(PublicationFrequency.DISABLED);
        if (enableSecondSchedule) {
        	schedule2.setStartDate(date);
        	schedule2.setPublicationFrequency(PublicationFrequency.DAILY);
        	schedule2.setNextReportDate(date.plusYears(1));
        }
        etsSection2.setPublicationSchedule(schedule2);
        
        return List.of(etsSection1, etsSection2);
    }
}
