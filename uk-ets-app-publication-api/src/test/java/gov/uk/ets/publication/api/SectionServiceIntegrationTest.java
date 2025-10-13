package gov.uk.ets.publication.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import gov.uk.ets.commons.s3.client.S3ClientService;
import gov.uk.ets.publication.api.domain.PublicationSchedule;
import gov.uk.ets.publication.api.domain.ReportFile;
import gov.uk.ets.publication.api.domain.Section;
import gov.uk.ets.publication.api.error.UkEtsPublicationApiException;
import gov.uk.ets.publication.api.file.upload.error.FileUploadException;
import gov.uk.ets.publication.api.file.upload.repository.ReportFilesRepository;
import gov.uk.ets.publication.api.model.DisplayType;
import gov.uk.ets.publication.api.model.PublicationFrequency;
import gov.uk.ets.publication.api.model.ReportPublicationStatus;
import gov.uk.ets.publication.api.model.SectionStatus;
import gov.uk.ets.publication.api.model.SectionType;
import gov.uk.ets.publication.api.repository.SectionRepository;
import gov.uk.ets.publication.api.service.SectionService;
import gov.uk.ets.publication.api.sort.SortParameters;
import gov.uk.ets.publication.api.web.model.FileInfoDto;
import gov.uk.ets.publication.api.web.model.ReportFileDto;
import gov.uk.ets.publication.api.web.model.SectionDto;
import gov.uk.ets.reports.model.ReportStatus;
import gov.uk.ets.reports.model.ReportType;
import gov.uk.ets.reports.model.RequestingSystem;
import gov.uk.ets.reports.model.messaging.ReportGenerationEvent;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.jdbc.JdbcTestUtils;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

/**
 * Right now S3 client is mocked, only the integration with the db is tested here.
 */
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class SectionServiceIntegrationTest extends BasePostgresFixture {

    private static final String TEST_TITLE_1 = "Test Title 1";
    private static final String TEST_SUMMARY_1 = "Test Summary 1";
    private static final String TEST_TITLE_2 = "Test Title 2";
    private static final String TEST_SUMMARY_2 = "Test Summary 2";
    private static final String UPDATED_TITLE = "Updated Title";
    private static final String UPDATED_SUMMARY = "Updated Summary";
    private static final String MANY_FILES_TITLE_1 = "RREG1_GB_2025_1_1";
    private static final String MANY_FILES_TITLE_2 = "RREG1_GB_2025_2_1";
    private static final String MANY_FILES_UPDATED_TITLE = "test_2025_1_1";
    private static final String BATCH_ID = "batch ID";
    private static final String FILE_LOCATION = "s3:/bucket/ets-reports/section2/filename";

    @Autowired
    private SectionService sectionService;
    
    @Autowired
    private SectionRepository sectionRepository;
    
    @Autowired
    private ReportFilesRepository reportFilesRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @MockitoBean
    S3ClientService s3ClientService;
    
    //Indicates the number of sections loaded via liquibase
    long sectionsCount = 0;

    @BeforeAll
    public void setUp() {
        sectionsCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, "section");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "report_file");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "publication_schedule");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "section");
        
        createInitialSections();
        createPublishedFilesForSections();  
    }
    
    @BeforeEach
    void setupEach() throws IOException, ExecutionException, InterruptedException {
        when(s3ClientService.downloadFile(anyString(), anyString()))
            .thenReturn(
                new ResponseInputStream<>(
                GetObjectResponse.builder().build(),
                AbortableInputStream.create(IOUtils.toInputStream("test", "UTF-8"))
                )
            );
    }
    
    @Test
    @Order(1)
    public void shouldRetrieveOnlyEtsSections() {
        List<SectionDto> sections = sectionService.getSections(SectionType.ETS);

        int allSectionsCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, "section");
        assertThat(allSectionsCount).isEqualTo(4);
        assertThat(sections).hasSize(2);
        assertThat(sections).extracting(SectionDto::getId).containsOnly(15L, 16L);
        assertThat(sections).extracting(SectionDto::getTitle).containsOnly(TEST_TITLE_1, TEST_TITLE_2);
        assertThat(sections).extracting(SectionDto::getSummary).containsOnly(TEST_SUMMARY_1, TEST_SUMMARY_2);
        assertThat(sections).extracting(SectionDto::getReportType).containsOnly(ReportType.R0008, ReportType.R0009);
    }

    @Test
    @Order(2)
    public void shouldRetrieveOnlyKpSections() {
        List<SectionDto> sections = sectionService.getSections(SectionType.KP);

        int allSectionsCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, "section");
        assertThat(allSectionsCount).isEqualTo(4);
        assertThat(sections).hasSize(2);
        assertThat(sections).extracting(SectionDto::getId).containsOnly(17L, 18L);
        assertThat(sections).extracting(SectionDto::getTitle).containsOnly(TEST_TITLE_1, TEST_TITLE_2);
        assertThat(sections).extracting(SectionDto::getSummary).containsOnly(TEST_SUMMARY_1, TEST_SUMMARY_2);
        assertThat(sections).extracting(SectionDto::getReportType).containsOnly(ReportType.R0010, ReportType.R0011);
    }
    
    @Test
    @Order(3)
    public void shouldFailRetrievingNotExistingSection() {
        assertThrows(IllegalArgumentException.class, () -> sectionService.getSection(500L));
    }
    
    @Test
    @Order(4)
    public void shouldRetrieveSectionDetails() {
        SectionDto section = sectionService.getSection(16L);

        assertThat(section).extracting(SectionDto::getId).isEqualTo(16L);
        assertThat(section).extracting(SectionDto::getTitle).isEqualTo(TEST_TITLE_2);
        assertThat(section).extracting(SectionDto::getSummary).isEqualTo(TEST_SUMMARY_2);
        assertThat(section).extracting(SectionDto::getReportType).isEqualTo(ReportType.R0009);
    }

    @Test
    @Order(5)
    public void shouldFailUpdatingSectionDetails_idCannotBeNull() {
        SectionDto request = new SectionDto();
        request.setId(null);
        request.setPublicationFrequency(PublicationFrequency.DAILY);
        assertThrows(InvalidDataAccessApiUsageException.class, () -> sectionService.updateSection(request));       
    }
    
    @Test
    @Order(6)
    public void shouldUpdateSectionDetails() {
        // new schedule
        LocalDateTime now = LocalDateTime.now();
        SectionDto request = new SectionDto();
        request.setId(16L);
        request.setTitle(UPDATED_TITLE);
        request.setSummary(UPDATED_SUMMARY);
        request.setDisplayType(DisplayType.ONE_FILE_PER_YEAR);
        request.setPublicationStart(now.toLocalDate());
        request.setPublicationTime(now.toLocalTime());
        request.setPublicationFrequency(PublicationFrequency.DAILY);
        request.setReportType(ReportType.R0044);
        sectionService.updateSection(request);
        SectionDto section1 = sectionService.getSection(16L);
        assertThat(section1).extracting(SectionDto::getId).isEqualTo(16L);
        assertThat(section1).extracting(SectionDto::getTitle).isEqualTo(UPDATED_TITLE);
        assertThat(section1).extracting(SectionDto::getSummary).isEqualTo(UPDATED_SUMMARY);
        assertThat(section1).extracting(SectionDto::getPublicationFrequency).isEqualTo(PublicationFrequency.DAILY);
        assertThat(section1).extracting(SectionDto::getEveryXDays).isNull();
        assertThat(section1).extracting(SectionDto::getReportType).isEqualTo(ReportType.R0044);
        
        // existing schedule
        request.setDisplayType(DisplayType.ONE_FILE_PER_YEAR);
        request.setPublicationFrequency(PublicationFrequency.EVERY_X_DAYS);
        request.setEveryXDays(15);
        request.setReportType(ReportType.R0020);
        sectionService.updateSection(request);
        SectionDto section2 = sectionService.getSection(16L);
        assertThat(section2).extracting(SectionDto::getId).isEqualTo(16L);
        assertThat(section2).extracting(SectionDto::getPublicationFrequency).isEqualTo(PublicationFrequency.EVERY_X_DAYS);
        assertThat(section2).extracting(SectionDto::getEveryXDays).isEqualTo(15);
        assertThat(section2).extracting(SectionDto::getReportType).isEqualTo(ReportType.R0020); 
    }
    
    // the following 7 tests check that the nextReportDate is set correctly
    // for different cases of startDate + publication frequency
    @Test
    @Order(7)
    public void shouldUpdateSectionDetails_nextReportDateForNewSchedule() {
        LocalDateTime now = LocalDateTime.now();
        SectionDto request = new SectionDto();
        request.setId(15L);
        request.setTitle(UPDATED_TITLE);
        request.setSummary(UPDATED_SUMMARY);
        request.setDisplayType(DisplayType.ONE_FILE);
        request.setPublicationStart(now.toLocalDate());
        request.setPublicationTime(now.toLocalTime());
        request.setPublicationFrequency(PublicationFrequency.EVERY_X_DAYS);
        request.setEveryXDays(15);
        request.setReportType(ReportType.R0008);
        sectionService.updateSection(request);
        Section section = sectionRepository.findById(request.getId()).orElseThrow(() 
                -> new IllegalArgumentException("Section not found"));
        PublicationSchedule schedule = section.getPublicationSchedule();
        assertThat(section).extracting(Section::getId).isEqualTo(15L);
        assertThat(schedule).extracting(PublicationSchedule::getPublicationFrequency).isEqualTo(PublicationFrequency.EVERY_X_DAYS);
        assertThat(schedule).extracting(PublicationSchedule::getEveryXDays).isEqualTo(15);
        LocalDateTime nextReportDate = schedule.getNextReportDate().truncatedTo(ChronoUnit.MINUTES);
        // next report date should be 15 days later
        assertThat(nextReportDate).isEqualTo(now.plusDays(15).truncatedTo(ChronoUnit.MINUTES));        
    }
    
    @Test
    @Order(8)
    public void shouldUpdateSectionDetails_nextReportDateDaily() {
        LocalDateTime now = LocalDateTime.now();
        SectionDto request = new SectionDto();
        request.setId(15L);
        request.setTitle(UPDATED_TITLE);
        request.setSummary(UPDATED_SUMMARY);
        request.setDisplayType(DisplayType.ONE_FILE);
        // start date is 2 days in the past, at a later hour
        request.setPublicationStart(now.minusDays(2).toLocalDate());
        request.setPublicationTime(now.plusHours(2).toLocalTime());
        request.setPublicationFrequency(PublicationFrequency.DAILY);
        request.setReportType(ReportType.R0008);
        sectionService.updateSection(request);
        Section section = sectionRepository.findById(request.getId()).orElseThrow(() 
                -> new IllegalArgumentException("Section not found"));
        PublicationSchedule schedule = section.getPublicationSchedule();
        assertThat(section).extracting(Section::getId).isEqualTo(15L);
        assertThat(schedule).extracting(PublicationSchedule::getPublicationFrequency).isEqualTo(PublicationFrequency.DAILY);
        LocalDateTime nextReportDate = schedule.getNextReportDate().truncatedTo(ChronoUnit.MINUTES);
        // next report date should be today, two hours later
        assertThat(nextReportDate).isEqualTo(now.plusHours(2).truncatedTo(ChronoUnit.MINUTES));        
    }
    
    @Test
    @Order(9)
    public void shouldUpdateSectionDetails_nextReportDateDaily2() {
        LocalDateTime now = LocalDateTime.now();
        SectionDto request = new SectionDto();
        request.setId(15L);
        request.setTitle(UPDATED_TITLE);
        request.setSummary(UPDATED_SUMMARY);
        request.setDisplayType(DisplayType.ONE_FILE);
        // start date is 2 days in the past, at an earlier hour
        request.setPublicationStart(now.minusDays(2).toLocalDate());
        request.setPublicationTime(now.minusHours(2).toLocalTime());
        request.setPublicationFrequency(PublicationFrequency.DAILY);
        request.setReportType(ReportType.R0008);
        sectionService.updateSection(request);
        Section section = sectionRepository.findById(request.getId()).orElseThrow(() 
                -> new IllegalArgumentException("Section not found"));
        PublicationSchedule schedule = section.getPublicationSchedule();
        assertThat(section).extracting(Section::getId).isEqualTo(15L);
        assertThat(schedule).extracting(PublicationSchedule::getPublicationFrequency).isEqualTo(PublicationFrequency.DAILY);
        LocalDateTime nextReportDate = schedule.getNextReportDate().truncatedTo(ChronoUnit.MINUTES);
        // next report date should be tomorrow, two hours earlier
        assertThat(nextReportDate).isEqualTo(now.plusDays(1).minusHours(2).truncatedTo(ChronoUnit.MINUTES));        
    }
    
    @Test
    @Order(10)
    public void shouldUpdateSectionDetails_nextReportDateYearly() {
        LocalDateTime now = LocalDateTime.now();
        SectionDto request = new SectionDto();
        request.setId(15L);
        request.setTitle(UPDATED_TITLE);
        request.setSummary(UPDATED_SUMMARY);
        request.setDisplayType(DisplayType.ONE_FILE);
        // start date is 2 years in the past, at a later hour
        request.setPublicationStart(now.minusYears(2).toLocalDate());
        request.setPublicationTime(now.plusHours(1).toLocalTime());
        request.setPublicationFrequency(PublicationFrequency.YEARLY);
        request.setReportType(ReportType.R0008);
        sectionService.updateSection(request);
        Section section = sectionRepository.findById(request.getId()).orElseThrow(() 
                -> new IllegalArgumentException("Section not found"));
        PublicationSchedule schedule = section.getPublicationSchedule();
        assertThat(section).extracting(Section::getId).isEqualTo(15L);
        assertThat(schedule).extracting(PublicationSchedule::getPublicationFrequency).isEqualTo(PublicationFrequency.YEARLY);
        LocalDateTime nextReportDate = schedule.getNextReportDate().truncatedTo(ChronoUnit.MINUTES);
        
        if (now.getHour() == 23) {
            // adding one hour to 23:XX will change the day
            assertThat(nextReportDate).isEqualTo(now.plusYears(1).minusDays(1).plusHours(1).truncatedTo(ChronoUnit.MINUTES));
        } else {
            // next report date should be today, two hours later
            assertThat(nextReportDate).isEqualTo(now.plusHours(1).truncatedTo(ChronoUnit.MINUTES)); 
        }
               
    }
    
    @Test
    @Order(11)
    public void shouldUpdateSectionDetails_nextReportDateYearly2() {
        LocalDateTime now = LocalDateTime.now();
        SectionDto request = new SectionDto();
        request.setId(15L);
        request.setTitle(UPDATED_TITLE);
        request.setSummary(UPDATED_SUMMARY);
        request.setDisplayType(DisplayType.ONE_FILE);
        // start date is 2 days later
        request.setPublicationStart(now.plusDays(2).toLocalDate());
        request.setPublicationTime(now.toLocalTime());
        request.setPublicationFrequency(PublicationFrequency.YEARLY);
        request.setReportType(ReportType.R0008);
        sectionService.updateSection(request);
        Section section = sectionRepository.findById(request.getId()).orElseThrow(() 
                -> new IllegalArgumentException("Section not found"));
        PublicationSchedule schedule = section.getPublicationSchedule();
        assertThat(section).extracting(Section::getId).isEqualTo(15L);
        assertThat(schedule).extracting(PublicationSchedule::getPublicationFrequency).isEqualTo(PublicationFrequency.YEARLY);
        LocalDateTime nextReportDate = schedule.getNextReportDate().truncatedTo(ChronoUnit.MINUTES);
        // next report date should be next year
        assertThat(nextReportDate).isEqualTo(now.plusDays(2).plusYears(1).truncatedTo(ChronoUnit.MINUTES));        
    }
    
    @Test
    @Order(12)
    public void shouldUpdateSectionDetails_nextReportDateEveryXDays() {
        LocalDateTime now = LocalDateTime.now();
        SectionDto request = new SectionDto();
        request.setId(15L);
        request.setTitle(UPDATED_TITLE);
        request.setSummary(UPDATED_SUMMARY);
        request.setDisplayType(DisplayType.ONE_FILE);
        // start date is 2 years in the past
        request.setPublicationStart(now.minusYears(2).toLocalDate());
        request.setPublicationTime(now.plusHours(1).toLocalTime());
        request.setPublicationFrequency(PublicationFrequency.EVERY_X_DAYS);
        request.setEveryXDays(4);
        request.setReportType(ReportType.R0008);
        sectionService.updateSection(request);
        Section section = sectionRepository.findById(request.getId()).orElseThrow(() 
                -> new IllegalArgumentException("Section not found"));
        PublicationSchedule schedule = section.getPublicationSchedule();
        assertThat(section).extracting(Section::getId).isEqualTo(15L);
        assertThat(schedule).extracting(PublicationSchedule::getPublicationFrequency).isEqualTo(PublicationFrequency.EVERY_X_DAYS);
        LocalDateTime nextReportDate = schedule.getNextReportDate().truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime startDate = now.getHour() == 23 
                ? LocalDateTime.of(request.getPublicationStart(), request.getPublicationTime()).minusDays(1)
                : LocalDateTime.of(request.getPublicationStart(), request.getPublicationTime());
        int daysBetween = (int) ChronoUnit.DAYS.between(startDate, now);
        int daysSinceLastReport = daysBetween % 4;
        if (daysSinceLastReport != 0) {
            assertThat(nextReportDate).isEqualTo(now.plusDays(4 - daysSinceLastReport).plusHours(1).truncatedTo(ChronoUnit.MINUTES));
        } else {
            assertThat(nextReportDate).isEqualTo(now.plusHours(1).truncatedTo(ChronoUnit.MINUTES));
        }
    }
    
    @Test
    @Order(13)
    public void shouldUpdateSectionDetails_nextReportDateEveryXDays2() {
        LocalDateTime now = LocalDateTime.now();
        SectionDto request = new SectionDto();
        request.setId(15L);
        request.setTitle(UPDATED_TITLE);
        request.setSummary(UPDATED_SUMMARY);
        request.setDisplayType(DisplayType.ONE_FILE);
        // start date is 10 days in the past, one hour earlier
        request.setPublicationStart(now.minusDays(10).toLocalDate());
        request.setPublicationTime(now.minusHours(1).toLocalTime());
        request.setPublicationFrequency(PublicationFrequency.EVERY_X_DAYS);
        request.setEveryXDays(10);
        request.setReportType(ReportType.R0008);
        sectionService.updateSection(request);
        Section section = sectionRepository.findById(request.getId()).orElseThrow(() 
                -> new IllegalArgumentException("Section not found"));
        PublicationSchedule schedule = section.getPublicationSchedule();
        assertThat(section).extracting(Section::getId).isEqualTo(15L);
        assertThat(schedule).extracting(PublicationSchedule::getPublicationFrequency).isEqualTo(PublicationFrequency.EVERY_X_DAYS);
        LocalDateTime nextReportDate = schedule.getNextReportDate().truncatedTo(ChronoUnit.MINUTES);
        if (now.getHour() == 00) {
            // subtracting one hour from 00:XX will change the day
            assertThat(nextReportDate).isEqualTo(now.plusDays(1).minusHours(1).truncatedTo(ChronoUnit.MINUTES));
        } else {
            // next report date should be in 10 days, one hour earlier
            assertThat(nextReportDate).isEqualTo(now.plusDays(10).minusHours(1).truncatedTo(ChronoUnit.MINUTES)); 
        }
    }
    
    @Test
    @Order(14)
    public void shouldRetrieveSectionPublishedFiles() {
        SortParameters sort = new SortParameters();
        sort.setSortDirection(Sort.Direction.DESC);
        sort.setSortField("fileName");
        List<ReportFileDto> files = sectionService.getFiles(15L, sort);
        
        int allSectionsCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, "report_file");
        assertThat(allSectionsCount).isEqualTo(8);
        assertThat(files).hasSize(2);
        assertThat(files).extracting(ReportFileDto::getApplicableForYear).containsOnly(2029, 2030);
        assertThat(files).extracting(ReportFileDto::getFileName).containsOnly(TEST_TITLE_1, TEST_TITLE_2);
    }
 
    @Test
    @Order(15)
    public void shouldFailToUnpublishFile_fileNotExists() {
        ReportFileDto file = new ReportFileDto();
        file.setId(500L);
        file.setFileName(TEST_TITLE_1);
        file.setApplicableForYear(2029);
        assertThrows(IllegalArgumentException.class, () -> sectionService.unpublishFile(file));
    }
    
    @Test
    @Order(16)
    public void shouldUnpublishFile() {
        ReportFileDto file = new ReportFileDto();
        file.setId(1L);
        file.setFileName(TEST_TITLE_1);
        file.setApplicableForYear(2029);
        sectionService.unpublishFile(file);
        // verify that unpublished file is no longer retrieved
        SortParameters sort = new SortParameters();
        sort.setSortDirection(Sort.Direction.DESC);
        sort.setSortField("fileName");
        List<ReportFileDto> files = sectionService.getFiles(15L, sort);
        assertThat(files).hasSize(1);
        assertThat(files).extracting(ReportFileDto::getApplicableForYear).containsOnly(2030);
        assertThat(files).extracting(ReportFileDto::getFileName).containsOnly(TEST_TITLE_2);
    }    
    
    @Test
    @Order(17)
    public void shouldFailToSubmitFile_fileNotExists() {
        FileInfoDto dto = new FileInfoDto(100L, "test", 2021, null, 500L);
        assertThrows(FileUploadException.class, () -> sectionService.submitFile(dto));
    }
    
    @Test
    @Order(18)
    public void shouldFailToSubmitFile_sectionNotExists() {
        FileInfoDto dto = new FileInfoDto(1L, "test", 2021, null, 500L);
        assertThrows(IllegalArgumentException.class, () -> sectionService.submitFile(dto));
    }
    
    @Test
    @Order(19)
    public void shouldFailToSubmitFile_sameNamePublishedInDifferentYear() {
        ReportFile newFile = new ReportFile();
        newFile.setSection(sectionRepository.findById(16L).orElseThrow(() 
                -> new IllegalArgumentException("Section not found")));
        newFile.setFileName(TEST_TITLE_2);
        ReportFile savedFile = reportFilesRepository.save(newFile);
        FileInfoDto dto = new FileInfoDto(savedFile.getId(), TEST_TITLE_2, 2029, null, 16L);
        assertThrows(FileUploadException.class, () -> sectionService.submitFile(dto));
    }
    
    @Test
    @Order(20)
    public void shouldPublishFileAndUnpublishPrevious() {
        ReportFile newFile = new ReportFile();
        newFile.setSection(sectionRepository.findById(16L).orElseThrow(() 
                -> new IllegalArgumentException("Section not found")));
        newFile.setFileName(UPDATED_TITLE);
        ReportFile savedFile = reportFilesRepository.save(newFile);
        FileInfoDto dto = new FileInfoDto(savedFile.getId(), UPDATED_TITLE, 2029, null, 16L);
        sectionService.submitFile(dto);
        // verify that published file is retrieved, and the previous file for 2029 is unpublished
        SortParameters sort = new SortParameters();
        sort.setSortDirection(Sort.Direction.DESC);
        sort.setSortField("fileName");
        List<ReportFileDto> files = sectionService.getFiles(16L, sort);
        assertThat(files).hasSize(2);
        assertThat(files).extracting(ReportFileDto::getApplicableForYear).containsOnly(2029, 2030);
        assertThat(files).extracting(ReportFileDto::getFileName).containsOnly(UPDATED_TITLE, TEST_TITLE_2); 
    }
    
    @Test
    @Order(21)
    public void shouldPublishFileAndUnpublishPrevious_manyFiles() {
        ReportFile newFile = new ReportFile();
        newFile.setSection(sectionRepository.findById(18L).orElseThrow(() 
                -> new IllegalArgumentException("Section not found")));
        newFile.setFileName(MANY_FILES_UPDATED_TITLE);
        ReportFile savedFile = reportFilesRepository.save(newFile);
        FileInfoDto dto = new FileInfoDto(savedFile.getId(), MANY_FILES_UPDATED_TITLE, 2021, null, 18L);
        sectionService.submitFile(dto);
        // unpublishing is determined based on filename, and both files have the same year,cp and version
        SortParameters sort = new SortParameters();
        sort.setSortDirection(Sort.Direction.DESC);
        sort.setSortField("fileName");
        List<ReportFileDto> files = sectionService.getFiles(18L, sort);
        assertThat(files).hasSize(2);
        assertThat(files).extracting(ReportFileDto::getFileName).containsOnly(MANY_FILES_UPDATED_TITLE, MANY_FILES_TITLE_2); 
    }
    
    @Test
    @Order(22)
    public void shouldPublishFile() {
        ReportFile newFile = new ReportFile();
        newFile.setSection(sectionRepository.findById(17L).orElseThrow(() 
                -> new IllegalArgumentException("Section not found")));
        newFile.setFileName(UPDATED_TITLE);
        ReportFile savedFile = reportFilesRepository.save(newFile);
        FileInfoDto dto = new FileInfoDto(savedFile.getId(), UPDATED_TITLE, 2028, null, 17L);
        sectionService.submitFile(dto);
        // verify that published file is retrieved, and the previous file for 2029 is unpublished
        SortParameters sort = new SortParameters();
        sort.setSortDirection(Sort.Direction.DESC);
        sort.setSortField("fileName");
        List<ReportFileDto> files = sectionService.getFiles(17L, sort);
        assertThat(files).hasSize(3);
        assertThat(files).extracting(ReportFileDto::getApplicableForYear).containsOnly(2028, 2029, 2030);
        assertThat(files).extracting(ReportFileDto::getFileName).containsOnly(TEST_TITLE_1, TEST_TITLE_2, UPDATED_TITLE);      
    }
 
    @Test
    @Order(23)
    public void shouldRetrieveFileContentOnDownload() {
        ReportFile file = new ReportFile();
        file.setId(2L);

        ReportFileDto fileDto = sectionService.downloadFile(file.getId());
        assertThat(fileDto.getData()).isNotEmpty();
    }
    
    @Test
    @Order(24)
    public void shouldThrowWhenDownloadingNonExistingFile() {

        assertThrows(IllegalArgumentException.class, () -> sectionService.downloadFile(50L));
    }

    @Test
    @Order(25)
    public void shouldProcessFailedReport_nonExistingFile() {
        ReportGenerationEvent event = ReportGenerationEvent.builder()
                .status(ReportStatus.FAILED)
                .id(1000L)
                .build();
        assertThrows(UkEtsPublicationApiException.class, () -> sectionService.processReportEvent(event));
    }
    
    @Test
    @Order(26)
    public void shouldProcessFailedReport() {
        ReportFile newFile = new ReportFile();
        newFile.setSection(sectionRepository.findById(18L).orElseThrow(() 
                -> new IllegalArgumentException("Section not found")));
        newFile.setFileName(TEST_TITLE_1);
        ReportFile savedFile = reportFilesRepository.save(newFile);
        ReportGenerationEvent event = ReportGenerationEvent.builder()
                .status(ReportStatus.FAILED)
                .id(savedFile.getId())
                .requestingSystem(RequestingSystem.PUBLICATION_API)
                .build();
        sectionService.processReportEvent(event);
        
        ReportFile updatedFile = reportFilesRepository.findById(savedFile.getId()).orElseThrow(() 
                -> new IllegalArgumentException("File not found"));
        assertThat(updatedFile).isNotNull();
        assertThat(updatedFile.getFileStatus()).isEqualTo(ReportPublicationStatus.FAILED_GENERATION);
        assertThat(updatedFile.getGeneratedOn()).isNotNull();
    }
    
    @Test
    @Order(27)
    public void shouldProcessGeneratedReport_oneFile() {
        ReportFile newFile = new ReportFile();
        newFile.setSection(sectionRepository.findById(15L).orElseThrow(() 
                -> new IllegalArgumentException("Section not found")));
        newFile.setFileName(TEST_TITLE_1);
        ReportFile savedFile = reportFilesRepository.save(newFile);
        ReportGenerationEvent event = ReportGenerationEvent.builder()
                .status(ReportStatus.DONE)
                .id(savedFile.getId())
                .requestingSystem(RequestingSystem.PUBLICATION_API)
                .fileLocation(FILE_LOCATION)
                .fileSize(10000L)
                .generationDate(LocalDateTime.now())
                .build();
        sectionService.processReportEvent(event);
        
        ReportFile updatedFile = reportFilesRepository.findById(savedFile.getId()).orElseThrow(() 
                -> new IllegalArgumentException("File not found"));
        assertThat(updatedFile).isNotNull();
        assertThat(updatedFile.getFileStatus()).isEqualTo(ReportPublicationStatus.PUBLISHED);
        assertThat(updatedFile.getGeneratedOn()).isNotNull();
        assertThat(updatedFile.getPublishedOn()).isNotNull();
        assertThat(updatedFile.getFileName()).isEqualTo("filename");
        assertThat(updatedFile.getFileSize()).isNotNull();
        // section should be in status published
        Section section = sectionRepository.findById(15L).orElseThrow(() 
                -> new IllegalArgumentException("Section not found"));
        assertThat(section.getStatus()).isEqualTo(SectionStatus.PUBLISHED);
        // any previously published files should be unpublished
        SortParameters sort = new SortParameters();
        sort.setSortDirection(Sort.Direction.DESC);
        sort.setSortField("fileName");
        List<ReportFileDto> files = sectionService.getFiles(15L, sort);
        assertThat(files).hasSize(1);
    }
    
    @Test
    @Order(28)
    public void shouldProcessGeneratedReport_oneFilePerYear() {
        // add two files with the same batch id
        ReportFile newFile1 = new ReportFile();
        newFile1.setSection(sectionRepository.findById(17L).orElseThrow(() 
                -> new IllegalArgumentException("Section not found")));
        newFile1.setFileName(TEST_TITLE_1);
        newFile1.setApplicableForYear(2029);
        newFile1.setBatchId(BATCH_ID);
        ReportFile savedFile1 = reportFilesRepository.save(newFile1);
        ReportFile newFile2 = new ReportFile();
        newFile2.setSection(sectionRepository.findById(17L).orElseThrow(() 
                -> new IllegalArgumentException("Section not found")));
        newFile2.setFileName(TEST_TITLE_1);
        newFile2.setApplicableForYear(2029);
        newFile2.setBatchId(BATCH_ID);
        ReportFile savedFile2 = reportFilesRepository.save(newFile2);
        // send event for first file in batch, status should become PENDING PUBLICATION
        ReportGenerationEvent event = ReportGenerationEvent.builder()
                .status(ReportStatus.DONE)
                .id(savedFile1.getId())
                .requestingSystem(RequestingSystem.PUBLICATION_API)
                .fileLocation(FILE_LOCATION)
                .fileSize(10000L)
                .generationDate(LocalDateTime.now())
                .build();
        sectionService.processReportEvent(event);
        
        ReportFile updatedFile1 = reportFilesRepository.findById(savedFile1.getId()).orElseThrow(() 
                -> new IllegalArgumentException("File not found"));
        assertThat(updatedFile1).isNotNull();
        assertThat(updatedFile1.getFileStatus()).isEqualTo(ReportPublicationStatus.PENDING_PUBLICATION);
        assertThat(updatedFile1.getGeneratedOn()).isNotNull();
        assertThat(updatedFile1.getPublishedOn()).isNull();
        assertThat(updatedFile1.getFileName()).isEqualTo("filename");
        assertThat(updatedFile1.getFileSize()).isNotNull();
        // any previously published files should STILL be published
        SortParameters sort = new SortParameters();
        sort.setSortDirection(Sort.Direction.DESC);
        sort.setSortField("fileName");
        List<ReportFileDto> files = sectionService.getFiles(17L, sort);
        assertThat(files).hasSize(3);
        
        // send event for second file in batch, batch should be published
        ReportGenerationEvent event2 = ReportGenerationEvent.builder()
                .status(ReportStatus.DONE)
                .id(savedFile2.getId())
                .requestingSystem(RequestingSystem.PUBLICATION_API)
                .fileLocation(FILE_LOCATION)
                .fileSize(10000L)
                .generationDate(LocalDateTime.now())
                .build();
        sectionService.processReportEvent(event2);
        
        ReportFile updatedFile2 = reportFilesRepository.findById(savedFile2.getId()).orElseThrow(() 
                -> new IllegalArgumentException("File not found"));
        assertThat(updatedFile2).isNotNull();
        assertThat(updatedFile2.getFileStatus()).isEqualTo(ReportPublicationStatus.PUBLISHED);
        assertThat(updatedFile2.getGeneratedOn()).isNotNull();
        assertThat(updatedFile2.getPublishedOn()).isNotNull();
        assertThat(updatedFile1.getFileName()).isEqualTo("filename");
        assertThat(updatedFile1.getFileSize()).isNotNull();
        // section should be in status published
        Section updatedSection = sectionRepository.findById(17L).orElseThrow(() 
                -> new IllegalArgumentException("Section not found"));
        assertThat(updatedSection.getStatus()).isEqualTo(SectionStatus.PUBLISHED);
        // new files should be published and all previous ones unpublished
        List<ReportFileDto> updatedFiles = sectionService.getFiles(17L, sort);
        assertThat(updatedFiles).hasSize(2);
    }

    @Test
    @Order(29)
    public void shouldFailToUpdateSectionDetails() {
        // new schedule
        LocalDateTime now = LocalDateTime.now();
        SectionDto request = new SectionDto();
        request.setId(16L);
        request.setTitle(UPDATED_TITLE);
        request.setSummary(UPDATED_SUMMARY);
        request.setDisplayType(DisplayType.ONE_FILE_PER_YEAR);
        request.setPublicationStart(now.toLocalDate());
        request.setPublicationTime(now.toLocalTime());
        request.setPublicationFrequency(PublicationFrequency.DAILY);
        request.setReportType(ReportType.R0009);

        assertThrows(UkEtsPublicationApiException.class, () -> sectionService.updateSection(request));
    }

    // initialize DB with two ETS and two KP sections
    private void createInitialSections() {
        Section etsSection1 = new Section();
        etsSection1.setSectionType(SectionType.ETS);
        etsSection1.setDisplayOrder(1);
        etsSection1.setTitle(TEST_TITLE_1);
        etsSection1.setSummary(TEST_SUMMARY_1);
        etsSection1.setDisplayType(DisplayType.ONE_FILE);
        etsSection1.setReportType(ReportType.R0008);

        Section etsSection2 = new Section();
        etsSection2.setSectionType(SectionType.ETS);
        etsSection2.setDisplayOrder(2);
        etsSection2.setTitle(TEST_TITLE_2);
        etsSection2.setSummary(TEST_SUMMARY_2);
        etsSection2.setDisplayType(DisplayType.ONE_FILE_PER_YEAR);
        etsSection2.setReportType(ReportType.R0009);

        Section kpSection1 = new Section();
        kpSection1.setSectionType(SectionType.KP);
        kpSection1.setDisplayOrder(1);
        kpSection1.setTitle(TEST_TITLE_1);
        kpSection1.setSummary(TEST_SUMMARY_1);
        kpSection1.setDisplayType(DisplayType.ONE_FILE_PER_YEAR);
        kpSection1.setReportType(ReportType.R0010);

        Section kpSection2 = new Section();
        kpSection2.setSectionType(SectionType.KP);
        kpSection2.setDisplayOrder(2);
        kpSection2.setTitle(TEST_TITLE_2);
        kpSection2.setSummary(TEST_SUMMARY_2);
        kpSection2.setDisplayType(DisplayType.MANY_FILES);
        kpSection2.setReportType(ReportType.R0011);

        sectionRepository.saveAll(List.of(etsSection1, etsSection2, kpSection1, kpSection2));
    }
    
    private void createPublishedFilesForSections() {
        sectionRepository.findAll().forEach(section -> {
            ReportFile file1 = new ReportFile();
            file1.setFileName(
                    DisplayType.MANY_FILES.equals(section.getDisplayType()) ? MANY_FILES_TITLE_1 : TEST_TITLE_1);
            file1.setApplicableForYear(2029);
            file1.setFileStatus(ReportPublicationStatus.PUBLISHED);
            file1.setFileLocation(FILE_LOCATION);
            file1.setSection(section);

            ReportFile file2 = new ReportFile();
            file2.setFileName(
                    DisplayType.MANY_FILES.equals(section.getDisplayType()) ? MANY_FILES_TITLE_2 : TEST_TITLE_2);
            file2.setApplicableForYear(2030);
            file2.setFileStatus(ReportPublicationStatus.PUBLISHED);
            file2.setFileLocation(FILE_LOCATION);
            file2.setSection(section);
            reportFilesRepository.saveAll(List.of(file1, file2));
        });
    }
}
