package gov.uk.ets.publication.api.migration;

import com.google.common.io.ByteStreams;
import gov.uk.ets.commons.s3.client.S3ClientService;
import gov.uk.ets.publication.api.domain.Section;
import gov.uk.ets.publication.api.error.UkEtsPublicationApiException;
import gov.uk.ets.publication.api.migration.domain.MigratorHistory;
import gov.uk.ets.publication.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.publication.api.migration.domain.MigratorName;
import gov.uk.ets.publication.api.model.SectionType;
import gov.uk.ets.publication.api.repository.SectionRepository;
import gov.uk.ets.publication.api.service.publishing.PublicReportsHtmlGenerator;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Log4j2
public class HtmlPublicationMigrator {

    @Value("${aws.s3.publication.bucket.name}")
    private String bucketName;
    @Value("${application.url}")
    String applicationUrl;
    private final MigratorHistoryRepository migratorHistoryRepository;
    private final S3ClientService s3ClientService;
    private final ResourceLoader resourceLoader;
    private final SectionRepository sectionRepository;
    private final PublicReportsHtmlGenerator htmlGenerator;

    private static final String ERROR_READING_RESOURCE = "Error while reading the resource";
    private static final String ETS_TEMPLATE = "/ets-reports.ftl";
    private static final String KP_TEMPLATE = "/kp-reports.ftl";
    private static final String ETS_PATH = "ets-reports.html";
    private static final String KP_PATH = "kp-reports.html";
    private static final String KP_REPORTS_PATH = "kp-reports/section";
    private static final String ETS_REPORTS_PATH = "ets-reports/section";
    private static final String SECTION = "section";

    @Transactional
    public void migrate() {
        log.info("Starting migration of public site pages");
        List<MigratorHistory> migratorHistories = migratorHistoryRepository.findByMigratorName(
            MigratorName.PUBLICATION_SITE_MIGRATOR);
        if (migratorHistories.size() > 0) {
            log.info("Migration of public pages has already performed previously, skipping.");
            return;
        }

        createAndUploadInitialFiles();
        createAndUploadEtsAndKpIndexPages();
        createAndUploadSections();

        MigratorHistory migratorHistory = new MigratorHistory();
        migratorHistory.setMigratorName(MigratorName.PUBLICATION_SITE_MIGRATOR);
        migratorHistory.setCreatedOn(LocalDateTime.now());
        migratorHistoryRepository.save(migratorHistory);
        log.info("Migration of public pages completed");
    }

    private void createAndUploadInitialFiles() {
        Stream.of(InitialFileAssets.values()).forEach(ia -> s3ClientService.uploadFile(
            bucketName, ia.getFilePath(),
            loadContentsFromResource(ia.getFilePath()), ia.getContentType()));
    }

    private void createAndUploadEtsAndKpIndexPages() {
        final Map<String, Object> params = new HashMap<>();
        params.put("applicationUrl", applicationUrl);
        String etsPage = htmlGenerator.createFromTemplate(ETS_TEMPLATE, params);
        String kpPage = htmlGenerator.createFromTemplate(KP_TEMPLATE, params);
        s3ClientService.uploadHtmlFile(bucketName, ETS_PATH, etsPage.getBytes());
        s3ClientService.uploadHtmlFile(bucketName, KP_PATH, kpPage.getBytes());
    }

    private void createAndUploadSections() {
        List<Section> etsSections = sectionRepository.findBySectionType(SectionType.ETS);
        List<Section> kpSections = sectionRepository.findBySectionType(SectionType.KP);

        etsSections.forEach(es -> s3ClientService.uploadHtmlFile(
                                    bucketName, getSectionPathFromSectionType(es), htmlGenerator.generateSection(es)));
        kpSections.forEach(kps -> s3ClientService.uploadHtmlFile(
            bucketName, getSectionPathFromSectionType(kps), htmlGenerator.generateSection(kps)));
    }

    private byte[] loadContentsFromResource(String resourceName) {
        try (InputStream inputStream =
                 resourceLoader.getResource("classpath:templates/public-site-migration/" + resourceName)
                               .getInputStream()) {
            return ByteStreams.toByteArray(inputStream);
        } catch (IOException e) {
            throw new UkEtsPublicationApiException(ERROR_READING_RESOURCE, e);
        }
    }

    private String getPathFromSectionType(Section section) {
        if (section.getSectionType().equals(SectionType.ETS)) {
            return ETS_REPORTS_PATH + section.getDisplayOrder() + "/";
        }
        return KP_REPORTS_PATH + section.getDisplayOrder() + "/";
    }

    private String getSectionPathFromSectionType(Section section) {
        return getPathFromSectionType(section) + SECTION + section.getDisplayOrder() + ".html";
    }
}
