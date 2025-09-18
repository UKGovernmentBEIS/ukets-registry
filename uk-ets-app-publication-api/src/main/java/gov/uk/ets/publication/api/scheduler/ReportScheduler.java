package gov.uk.ets.publication.api.scheduler;

import gov.uk.ets.publication.api.domain.PublicationSchedule;
import gov.uk.ets.publication.api.domain.ReportFile;
import gov.uk.ets.publication.api.domain.Section;
import gov.uk.ets.publication.api.error.UkEtsPublicationApiException;
import gov.uk.ets.publication.api.file.upload.repository.ReportFilesRepository;
import gov.uk.ets.publication.api.model.DisplayType;
import gov.uk.ets.publication.api.model.PublicationFrequency;
import gov.uk.ets.publication.api.model.ReportPublicationStatus;
import gov.uk.ets.publication.api.model.SectionStatus;
import gov.uk.ets.publication.api.repository.PublicationScheduleRepository;
import gov.uk.ets.publication.api.repository.SectionRepository;
import gov.uk.ets.publication.api.service.ReportGenerationService;
import gov.uk.ets.publication.api.service.SectionUtil;
import gov.uk.ets.reports.model.ReportQueryInfo;
import gov.uk.ets.reports.model.ReportType;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Log4j2
public class ReportScheduler {

    private final ReportGenerationService reportGenerationService;
    private final SectionRepository sectionRepository;
    private final ReportFilesRepository reportFilesRepository;
    private final PublicationScheduleRepository scheduleRepository;
    private final SectionUtil sectionUtil;

    @Scheduled(cron = "${scheduler.publication.start}")
    @SchedulerLock(name = "publicationSchedulerLock", lockAtLeastFor = "500ms")
    public void execute() {
        LockAssert.assertLocked();
        log.info("Scheduled section checking for potential report generation...");
        LocalDateTime truncatedNow = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime now = LocalDateTime.now();
        
        List<Section> sections = sectionRepository.findAll();
        for (Section section: sections) {
            PublicationSchedule schedule = section.getPublicationSchedule();
            // this is needed in order to recalibrate the next report date, if for some reason there is an old date
            // eg due to the publication-api being down at the time
            if (schedule.getNextReportDate() != null 
                    && schedule.getNextReportDate().truncatedTo(ChronoUnit.MINUTES).isBefore(truncatedNow)) {
                updateNextReportDateTime(schedule);
            }
            if (schedule != null && isEqualOrAfterStartDate(truncatedNow, schedule.getStartDate())
                    && isGenerationTime(truncatedNow, schedule)) {
                // update next report date
                if (schedule.getNextReportDate() != null 
                        && truncatedNow.isEqual(schedule.getNextReportDate().truncatedTo(ChronoUnit.MINUTES))) {
                    updateNextReportDateTime(schedule);
                }
                section.setStatus(SectionStatus.PUBLISHING);
                log.info("Publishing section...");
                if (section.getDisplayType().equals(DisplayType.ONE_FILE)) {
                    requestGenerationOfReport(truncatedNow, truncatedNow.getYear(), section, schedule, null);
                }
                if (section.getDisplayType().equals(DisplayType.ONE_FILE_PER_YEAR)) {
                    // If the first year is different among the reports, this logic should be moved to SectionUtil.
                    int firstYear = section.getReportType() == ReportType.R0050 ? 2025 : 2021;
                    Iterator<Integer> it = IntStream.rangeClosed(firstYear, truncatedNow.getYear()).boxed().iterator();
                    while (it.hasNext()) {
                        requestGenerationOfReport(truncatedNow, it.next(), section, schedule, section.getId() + "_" + now);
                    }
                }
                sectionRepository.save(section);
            }
        }

        log.info("Scheduled section checking ended");
    }

    private void requestGenerationOfReport(
            LocalDateTime date, Integer year, Section section, PublicationSchedule schedule, String batchId) {
        ReportFile file = new ReportFile();
        file.setSection(section);
        file.setApplicableForYear(year);
        file.setFileStatus(ReportPublicationStatus.GENERATING);
        file.setBatchId(batchId);
        ReportFile savedFile = reportFilesRepository.save(file);

        ReportQueryInfo queryInfo = new ReportQueryInfo();
        if (sectionUtil.isYearlyReport(section.getReportType())) {
            queryInfo.setYear((long) year);	
        }      
        queryInfo.setTo(schedule.getGenerationDate() != null ? Timestamp.valueOf(schedule.getGenerationDate().withYear(year)) : null);

        reportGenerationService.requestReport(
                section.getReportType(), savedFile.getId(), date, queryInfo);
    }

    private boolean isGenerationTime(LocalDateTime currentDateTime, PublicationSchedule schedule) {
        // compare times taking only hours and minutes into account, ignore seconds
        return !schedule.getPublicationFrequency().equals(PublicationFrequency.DISABLED) 
                && (currentDateTime.isEqual(schedule.getStartDate().truncatedTo(ChronoUnit.MINUTES))
                || (schedule.getNextReportDate() != null 
                && currentDateTime.isEqual(schedule.getNextReportDate().truncatedTo(ChronoUnit.MINUTES))));
    }

    private void updateNextReportDateTime(PublicationSchedule schedule) {
        switch (schedule.getPublicationFrequency()) {
            case DAILY:
                updateSchedule(schedule.getId(), schedule.getNextReportDate().plusDays(1));
                break;
            case YEARLY:
                updateSchedule(schedule.getId(), schedule.getNextReportDate().plusYears(1));
                break;
            case EVERY_X_DAYS:
                updateSchedule(schedule.getId(), 
                    schedule.getNextReportDate().plusDays(schedule.getEveryXDays()));
                break;
            default:
                break;
        }
    }
    
    private void updateSchedule(Long id, LocalDateTime newDate) {
        PublicationSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new UkEtsPublicationApiException(String
                        .format("Publication schedule not found for id: %s", id)));
        schedule.setNextReportDate(newDate);
        scheduleRepository.save(schedule);
    }
    
    private boolean isEqualOrAfterStartDate(LocalDateTime now, LocalDateTime date) {
        return date != null && (now.isEqual(date.truncatedTo(ChronoUnit.MINUTES)) 
                || now.isAfter(date.truncatedTo(ChronoUnit.MINUTES)));
    }
}
