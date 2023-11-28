package gov.uk.ets.publication.api.service;

import static gov.uk.ets.publication.api.Utils.convertByteAmountToHumanReadable;
import static java.util.stream.Collectors.toList;

import gov.uk.ets.commons.s3.client.S3ClientService;
import gov.uk.ets.publication.api.domain.PublicationSchedule;
import gov.uk.ets.publication.api.domain.ReportFile;
import gov.uk.ets.publication.api.domain.Section;
import gov.uk.ets.publication.api.error.UkEtsPublicationApiException;
import gov.uk.ets.publication.api.file.upload.error.FileUploadException;
import gov.uk.ets.publication.api.file.upload.repository.ReportFilesRepository;
import gov.uk.ets.publication.api.file.upload.services.PublicationUploadProcessor;
import gov.uk.ets.publication.api.model.DisplayType;
import gov.uk.ets.publication.api.model.ReportPublicationStatus;
import gov.uk.ets.publication.api.model.SectionStatus;
import gov.uk.ets.publication.api.model.SectionType;
import gov.uk.ets.publication.api.repository.PublicationScheduleRepository;
import gov.uk.ets.publication.api.repository.SectionRepository;
import gov.uk.ets.publication.api.service.publishing.PublicReportsHtmlGenerator;
import gov.uk.ets.publication.api.sort.SortParameters;
import gov.uk.ets.publication.api.web.model.FileInfoDto;
import gov.uk.ets.publication.api.web.model.ReportFileDto;
import gov.uk.ets.publication.api.web.model.SectionDto;
import gov.uk.ets.reports.model.ReportStatus;
import gov.uk.ets.reports.model.messaging.ReportGenerationEvent;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;


@Service
@RequiredArgsConstructor
@Log4j2
public class SectionService {

    @Value("${aws.s3.publication.bucket.name}")
    private String bucketName;

    @Value("${aws.s3.reports.bucket.name}")
    private String reportBucketName;

    private static final String UNPUBLISHED_FILES_ARCHIVE = "archive/";
    private static final String KP_REPORTS_PATH = "kp-reports/";
    private static final String ETS_REPORTS_PATH = "ets-reports/";
    private static final String SECTION = "section";

    private final SectionRepository sectionRepository;
    private final S3ClientService s3ClientService;
    private final PublicationUploadProcessor uploadProcessor;
    private final PublicationScheduleRepository publicationScheduleRepository;
    private final ReportFilesRepository reportFilesRepository;
    private final PublicReportsHtmlGenerator htmlGenerator;

    /**
     * Retrieve all sections of the specified type.
     */
    @Transactional(readOnly = true)
    public List<SectionDto> getSections(SectionType sectionType) {
        return sectionRepository.findBySectionType(sectionType).stream()
                .sorted(Comparator.comparing(Section::getDisplayOrder))
                .map(this::toSectionDto)
                .collect(toList());
    }

    /**
     * Retrieve section details.
     */
    @Transactional(readOnly = true)
    public SectionDto getSection(Long id) {
        Section section = sectionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(
                String.format("Section not found for id: %s", id)));
        return toSectionDto(section);
    }

    /**
     * Update details of specific section.
     */
    @Transactional
    public Long updateSection(SectionDto request) {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Section section = sectionRepository.findById(request.getId()).orElseThrow(() -> new IllegalArgumentException(
                String.format("Section not found for id: %s", request.getId())));
        LocalDateTime startDate = request.getPublicationStart() != null &&  request.getPublicationTime() != null
                ? LocalDateTime.of(request.getPublicationStart(), request.getPublicationTime())
                : null;
        if (section.getPublicationSchedule() != null) {
            PublicationSchedule schedule = section.getPublicationSchedule();
            schedule.setPublicationFrequency(request.getPublicationFrequency());
            schedule.setEveryXDays(request.getEveryXDays());
            schedule.setStartDate(startDate != null ? startDate : schedule.getStartDate());
            schedule.setGenerationDate(mergeCutOffDateAndTime(request));
            if (startDate != null) {
                schedule.setNextReportDate(calculateNextReportDate(request, now));
            }
            publicationScheduleRepository.save(schedule);
        } else if (request.getPublicationFrequency() != null) {
            PublicationSchedule schedule = new PublicationSchedule();
            schedule.setSection(section);
            schedule.setPublicationFrequency(request.getPublicationFrequency());
            schedule.setEveryXDays(request.getEveryXDays());
            schedule.setStartDate(startDate);
            if (startDate != null) {
                schedule.setNextReportDate(calculateNextReportDate(request, now));
            }
            publicationScheduleRepository.save(schedule);
        }
        
        toSection(request, section);
        section.setLastUpdated(LocalDateTime.now());
        sectionRepository.save(section);
        s3ClientService.uploadHtmlFile(bucketName, getSectionPathFromSectionType(section),
                                       htmlGenerator.generateSection(section));
        return section.getId();
    }

    
    /**
     * Calculate the next report date, depending on the current date and the user selected date and frequency.
     */
    private LocalDateTime calculateNextReportDate(SectionDto request, LocalDateTime now) {
        LocalDateTime startDate = LocalDateTime.of(request.getPublicationStart(), request.getPublicationTime());
        LocalDateTime startDateTruncated = startDate.truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime newDate = now
                .withHour(request.getPublicationTime().getHour())
                .withMinute(request.getPublicationTime().getMinute());
        switch (request.getPublicationFrequency()) {
            case DAILY:
                if (startDateTruncated.isAfter(now) || startDateTruncated.plusDays(1).isAfter(now)) {
                    return startDate.plusDays(1);
                } else {
                    return newDate.isBefore(now) ? newDate.plusDays(1) : newDate;
                }
            case YEARLY:
                if (startDateTruncated.isAfter(now) || startDateTruncated.plusYears(1).isAfter(now)) {
                    return startDate.plusYears(1);
                } else {
                    return startDateTruncated.withYear(newDate.getYear()).isBefore(now) 
                            ? startDate.withYear(newDate.getYear()).plusYears(1)
                            : startDate.withYear(newDate.getYear());
                }
            case EVERY_X_DAYS:
                if (startDateTruncated.isAfter(now) || startDateTruncated.plusDays(request.getEveryXDays()).isAfter(now)) {
                    return startDate.plusDays(request.getEveryXDays());
                } else {
                    int daysBetween = (int) ChronoUnit.DAYS.between(startDateTruncated, now);
                    int daysSinceLastReport = daysBetween % request.getEveryXDays();
                    if (daysSinceLastReport != 0) {
                        return newDate.plusDays((long) request.getEveryXDays() - daysSinceLastReport);
                    } else {
                        return newDate.isBefore(now) ? newDate.plusDays(request.getEveryXDays()) : newDate; 
                    }
                }
            default:
                break;
        }
        return null;
    }

    /**
     * List files associated with a section.
     * @param id  section id
     * @param sortParams sort parameters
     * @return report files for the specified section, sorted.
     */
    public List<ReportFileDto> getFiles(Long id, SortParameters sortParams) {
        sectionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(
                String.format("Section not found for id: %s", id)));
        Sort sortOrder = Sort.by(sortParams.getSortDirection(), sortParams.getSortField());
        return reportFilesRepository.findBySectionIdAndFileStatus(id, sortOrder, ReportPublicationStatus.PUBLISHED)
                .stream()
                .map(this::toFileDto)
                .collect(toList());
    }

    public FileInfoDto uploadFile(MultipartFile file, DisplayType displayType) {
        return uploadProcessor.loadAndVerifyFileIntegrity(file, displayType);
    }
    
    /**
     * Responsible for the submission of the persisted file to the S3 bucket.
     *
     * @param dto the file info DTO
     */
    @Transactional
    public void submitFile(FileInfoDto dto) {
        ReportFile file = reportFilesRepository.findById(dto.getId()).orElseThrow(
            () -> new FileUploadException("Error while processing the file"));
        Section section = sectionRepository.findById(dto.getSectionId()).orElseThrow(
            () -> new IllegalArgumentException(String.format("Section not found for id: %s", dto.getSectionId())));

        DisplayType type = section.getDisplayType();
        if (DisplayType.ONE_FILE.equals(type)) {
            findAndUnpublishFiles(section.getId(), null);
        } else if (DisplayType.ONE_FILE_PER_YEAR.equals(type)) {
            checkDuplicatePublishedFileName(section.getId(), dto.getYear(), file.getFileName());
            findAndUnpublishFiles(section.getId(), dto.getYear());
        } else if (DisplayType.MANY_FILES.equals(type)) {
            findAndUnpublishFileByYearCPAndVersion(section.getId(), dto.getFileName());
        }
        s3ClientService.uploadXlsxFile(
                bucketName, getFilePathFromSectionType(section, file.getFileName()), file.getFileData());
        file.setSection(section);
        if (dto.getFileName() != null) {
            file.setFileName(dto.getFileName());
        }
        file.setFileStatus(ReportPublicationStatus.PUBLISHED);
        file.setApplicableForYear(dto.getYear());
        file.setFileLocation(
                String.format("s3:%s/%s", bucketName, getFilePathFromSectionType(section, file.getFileName())));
        file.setPublishedOn(LocalDateTime.now());
        file.setFileData(null);
        reportFilesRepository.save(file);
        section.addReportFile(file);
        s3ClientService.uploadHtmlFile(bucketName, getSectionPathFromSectionType(section),
                                       htmlGenerator.generateSection(section));
    }

	/**
     * Unpublish a file.
     */
    @Transactional
    public Long unpublishFile(ReportFileDto file) {
        ReportFile reportFile = reportFilesRepository.findById(file.getId()).orElseThrow(
                () -> new IllegalArgumentException(String.format("File not found for id: %s", file.getId())));
        unpublishAction(reportFile);
        if (reportFile.getSection() != null) {
            s3ClientService.uploadHtmlFile(bucketName, getSectionPathFromSectionType(reportFile.getSection()),
                                           htmlGenerator.generateSection(reportFile.getSection()));
        }
        return reportFile.getId();
    }

    /**
     * Download a file from the S3 bucket.
     */
    public ReportFileDto downloadFile(Long id) {
        ReportFile file = reportFilesRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(
                String.format("File not found for id: %s", id)));
        String s3Location = file.getFileLocation();
        String fileLocation = file.getFileLocation().substring(s3Location.indexOf("/") + 1);
        try (ResponseInputStream<GetObjectResponse> responseInputStream = s3ClientService.downloadFile(bucketName,
                                                                                                       fileLocation)) {
            return toFileDtoWithContent(file, responseInputStream.readAllBytes());
        } catch (Exception e) {
            throw new UkEtsPublicationApiException(
                    String.format("Retrieving file %s from bucket %s failed. Cause: %s", fileLocation, bucketName,
                                  e.getCause().getMessage()));
        }
    }

    private ReportFileDto toFileDto(ReportFile file) {
        ReportFileDto fileDto = new ReportFileDto();
        fileDto.setId(file.getId());
        fileDto.setFileName(file.getFileName());
        fileDto.setApplicableForYear(file.getApplicableForYear());
        fileDto.setPublishedOn(file.getPublishedOn());        
        return fileDto;
    }

    private ReportFileDto toFileDtoWithContent(ReportFile file, byte[] readAllBytes) {
        ReportFileDto fileDto = toFileDto(file);
        fileDto.setData(readAllBytes);
        return fileDto;
    }
    
    private SectionDto toSectionDto(Section section) {
        SectionDto sectionDto = new SectionDto();
        sectionDto.setId(section.getId());
        sectionDto.setTitle(section.getTitle());
        sectionDto.setSummary(section.getSummary());
        sectionDto.setDisplayOrder(section.getDisplayOrder());
        sectionDto.setReportType(section.getReportType());
        sectionDto.setDisplayType(section.getDisplayType());
        if (section.getPublicationSchedule() != null) {
            sectionDto.setPublicationFrequency(section.getPublicationSchedule().getPublicationFrequency());
            sectionDto.setEveryXDays(section.getPublicationSchedule().getEveryXDays());
            if (section.getPublicationSchedule().getStartDate() != null) {
                sectionDto.setPublicationStartDateTime(section.getPublicationSchedule().getStartDate());
                sectionDto.setPublicationStart(section.getPublicationSchedule().getStartDate().toLocalDate());
                sectionDto.setPublicationTime(section.getPublicationSchedule().getStartDate().toLocalTime());
            }
            if (section.getPublicationSchedule().getGenerationDate() != null) {
                sectionDto.setGenerationDate(section.getPublicationSchedule().getGenerationDate().toLocalDate());
                sectionDto.setGenerationTime(section.getPublicationSchedule().getGenerationDate().toLocalTime());
            }          
        }
        sectionDto.setLastUpdated(section.getLastUpdated());
        // retrieve the publication date of the most recent file, TODO: optimize the query 
        // so that we don't have to iterate over all files every time the section list is loaded
        if (section.getReportFiles() != null && !section.getReportFiles().isEmpty()) {
            Optional<ReportFile> file =
                section.getReportFiles()
                       .stream()
                       .filter(s -> s.getFileStatus() == ReportPublicationStatus.PUBLISHED)
                       .min(Comparator.comparing(ReportFile::getPublishedOn,
                                                 Comparator.nullsLast(Comparator.reverseOrder())));
            file.ifPresent(reportFile -> sectionDto.setLastReportPublished(reportFile.getPublishedOn()));
        }      
        return sectionDto;
    }
    
    private Section toSection(SectionDto request, Section section) {
        section.setTitle((ObjectUtils.firstNonNull(request.getTitle(),section.getTitle())));
        section.setSummary(request.getSummary());
        section.setDisplayOrder((ObjectUtils.firstNonNull(request.getDisplayOrder(),section.getDisplayOrder())));
        section.setReportType(ObjectUtils.firstNonNull(request.getReportType(),section.getReportType()));
        section.setDisplayType((ObjectUtils.firstNonNull(request.getDisplayType(),section.getDisplayType())));
        return section;
    }

    private void findAndUnpublishFiles(Long id, Integer year) {
        if (year != null) {
            reportFilesRepository.findBySectionId(id)
            .stream()
            .filter(pubFile -> ReportPublicationStatus.PUBLISHED.equals(pubFile.getFileStatus()) 
                    && pubFile.getApplicableForYear().equals(year))
                .forEach(this::unpublishAction);
        } else {
            reportFilesRepository.findBySectionId(id)
            .stream()
            .filter(pubFile -> ReportPublicationStatus.PUBLISHED.equals(pubFile.getFileStatus()))
                .forEach(this::unpublishAction);
        }
    }
    
    private void unpublishAction(ReportFile file) {
        // move file under /archive in S3 bucket
        s3ClientService.copyFile(
            bucketName, getFilePathFromSectionType(file.getSection(), file.getFileName()),
            bucketName, UNPUBLISHED_FILES_ARCHIVE + getFilePathFromSectionType(
                file.getSection(), LocalDateTime.now() + "_" + file.getFileName()));

        s3ClientService.deleteFile(bucketName, getFilePathFromSectionType(file.getSection(), file.getFileName()));
        // mark file as UNPUBLISHED in DB
        file.setFileStatus(ReportPublicationStatus.UNPUBLISHED);
        reportFilesRepository.save(file);
    }

    /**
     * Processes incoming report events.
     */
    @Transactional
    public void processReportEvent(ReportGenerationEvent event) {
        if (event.getStatus() == ReportStatus.DONE) {
            processGeneratedReport(event);
        } else if (event.getStatus() == ReportStatus.FAILED) {
            processFailedReport(event);
        }
    }

    /**
     * Copies generated report from report-bucket to publication-bucket,
     * updates report in database with report generation metadata,
     * and triggers html update where applicable
     */
    private void processGeneratedReport(ReportGenerationEvent event) {
        ReportFile file = reportFilesRepository.findById(event.getId())
                                            .orElseThrow(() -> new UkEtsPublicationApiException(
                                                String.format("Report with id %s not found.", event.getId())));
        Section section = file.getSection();
        // if display type is one file per year, use PENDING_PUBLICATION as intermediate status
        file.setFileStatus(DisplayType.ONE_FILE_PER_YEAR.equals(section.getDisplayType())
                ? ReportPublicationStatus.PENDING_PUBLICATION
                : file.getFileStatus());
        String fileLocation = event.getFileLocation();
        file.setFileName(fileLocation.substring(fileLocation.lastIndexOf('/') + 1));
        file.setFileLocation(
                String.format("s3:%s/%s", bucketName, getFilePathFromSectionType(section, file.getFileName())));
        file.setFileSize(convertByteAmountToHumanReadable(event.getFileSize() * 1024));
        file.setGeneratedOn(event.getGenerationDate());
        reportFilesRepository.save(file);
        s3ClientService.copyFile(reportBucketName, file.getFileName(), bucketName,
                getFilePathFromSectionType(section, file.getFileName()));

        // if display type is one file per year, we need to check the entire batch
        if (DisplayType.ONE_FILE_PER_YEAR.equals(section.getDisplayType())) {
            batchPublication(file, section);
        } else if (DisplayType.ONE_FILE.equals(section.getDisplayType())) {
            findAndUnpublishFiles(section.getId(), null);
            file.setFileStatus(ReportPublicationStatus.PUBLISHED);
            file.setPublishedOn(LocalDateTime.now());
            reportFilesRepository.save(file);
            section.setStatus(SectionStatus.PUBLISHED);
            sectionRepository.save(section);
            s3ClientService.uploadHtmlFile(bucketName, getSectionPathFromSectionType(section),
                                           htmlGenerator.generateSection(section));
        }
    }

    private void batchPublication(ReportFile file, Section section) {
        List<ReportFile> filesInBatch = reportFilesRepository.findByBatchId(file.getBatchId());
        boolean isBatchReadyForPublication = filesInBatch
                .stream()
                .allMatch(f -> ReportPublicationStatus.PENDING_PUBLICATION.equals(f.getFileStatus()));
        if (isBatchReadyForPublication) {
            findAndUnpublishFiles(section.getId(), null);
            for (ReportFile reportfile: filesInBatch) {
                reportfile.setFileStatus(ReportPublicationStatus.PUBLISHED);
                reportfile.setPublishedOn(LocalDateTime.now());
            }
            reportFilesRepository.saveAll(filesInBatch);
            section.setStatus(SectionStatus.PUBLISHED);
            sectionRepository.save(section);
            s3ClientService.uploadHtmlFile(bucketName, getSectionPathFromSectionType(section),
                                           htmlGenerator.generateSection(section));
        }
    }

    private void processFailedReport(ReportGenerationEvent event) {
        ReportFile file = reportFilesRepository.findById(event.getId())
                                                 .orElseThrow(() -> new UkEtsPublicationApiException(
                                                     String.format("Report with id %s not found.", event.getId())));

        file.setFileStatus(ReportPublicationStatus.FAILED_GENERATION);
        file.setGeneratedOn(LocalDateTime.now());
        reportFilesRepository.save(file);
    }

    private String getPathFromSectionType(Section section) {
        if (section.getSectionType().equals(SectionType.ETS)) {
            return ETS_REPORTS_PATH + SECTION + section.getDisplayOrder() + "/";
        }
        return KP_REPORTS_PATH + SECTION + section.getDisplayOrder() + "/";
    }

    private String getFilePathFromSectionType(Section section, String filename) {
        return getPathFromSectionType(section) + filename;
    }

    private String getSectionPathFromSectionType(Section section) {
        return getPathFromSectionType(section) + SECTION + section.getDisplayOrder() + ".html";
    }
    
    private void checkDuplicatePublishedFileName(Long id, Integer year, String name) {
        List<ReportFile> files = reportFilesRepository.findBySectionId(id)
            .stream()
            .filter(pubFile -> ReportPublicationStatus.PUBLISHED.equals(pubFile.getFileStatus()) 
                && !pubFile.getApplicableForYear().equals(year)
                && pubFile.getFileName().equals(name))
            .collect(Collectors.toList());
        if (!files.isEmpty()) {
            throw new FileUploadException(
                "A file with the same name is already published for a different year.");
        }
    }
    
    private void findAndUnpublishFileByYearCPAndVersion(Long id, String fileName) {
        String yearCpVersion = getYearCpAndVersion(fileName);
        reportFilesRepository.findBySectionId(id)
        .stream()
        .filter(pubFile -> ReportPublicationStatus.PUBLISHED.equals(pubFile.getFileStatus())
                && getYearCpAndVersion(pubFile.getFileName()).equals(yearCpVersion))
            .forEach(this::unpublishAction);
    }

    private String getYearCpAndVersion(String fileName) {
        return fileName.substring(StringUtils.lastOrdinalIndexOf(fileName, "_", 3));
    }

    private LocalDateTime mergeCutOffDateAndTime(SectionDto sectionDto) {
        if (sectionDto.getGenerationDate() != null && sectionDto.getGenerationTime() != null) {
            return LocalDateTime.of(sectionDto.getGenerationDate(), sectionDto.getGenerationTime());
        }
        return null;
    }
}
