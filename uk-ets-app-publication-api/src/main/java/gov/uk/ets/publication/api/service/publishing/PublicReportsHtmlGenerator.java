package gov.uk.ets.publication.api.service.publishing;

import com.google.common.collect.Iterables;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import gov.uk.ets.publication.api.domain.ReportFile;
import gov.uk.ets.publication.api.domain.Section;
import gov.uk.ets.publication.api.error.UkEtsPublicationApiException;
import gov.uk.ets.publication.api.model.DisplayType;
import gov.uk.ets.publication.api.model.ReportPublicationStatus;
import gov.uk.ets.publication.api.model.SectionType;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import gov.uk.ets.reports.model.ReportType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

@Service
@RequiredArgsConstructor
public class PublicReportsHtmlGenerator {

    @Value("${publication.html.more-reports.size}")
    private int moreReportsSize;
    private final Configuration freemarkerConfiguration;

    private static final String ERROR_PROCESSING_TEMPLATE = "Error while processing the template";
    private static final String MAIN_SECTION_TEMPLATE = "/main-section.ftl";
    private static final String EMPTY_REPORT_TEMPLATE = "/empty-report-area.ftl";
    private static final String REPORTS_SECTION_TEMPLATE = "/reports-section.ftl";
    private static final String MORE_REPORTS_AREA_TEMPLATE = "/more-reports-area.ftl";
    private static final String ENDING_SECTION_AREA_TEMPLATE = "/ending-section-area.ftl";
    private static final String ETS_REPORT_SECTION = "/ets-reports/section";
    private static final String KP_REPORT_SECTION = "/kp-reports/section";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final ReportType PERIOD_REPORT = ReportType.R0017;


    /**
     * Creates a String representation of a publication section, consisting of
     * the main section attributes (title, summary) and the report files list.
     *
     * @param section the Section object.
     * @return the byte array of the string representation.
     */
    public byte[] generateSection(Section section) {
        final Map<String, Object> params = new HashMap<>();
        params.put("sectionTitle", section.getTitle());
        params.put("sectionType", section.getReportType());
        params.put("sectionSummary", section.getSummary());
        params.put("sectionPath", getSectionPath(section));
        List<ReportFile> reportFiles = section.getReportFiles()
                                              .stream()
                                              .filter(rf -> ReportPublicationStatus.PUBLISHED.equals(rf.getFileStatus()))
                                              .sorted(Comparator.comparing(ReportFile::getApplicableForYear).reversed())
                                              .toList();
        params.put("allReports", reportFiles);
        params.put("visibleReports", reportFiles);
        if (reportFiles.size() > moreReportsSize) {
           List[] listArray = splitList(reportFiles, moreReportsSize);
            params.put("visibleReports", listArray[0]);
        }
        params.put("isApplicableForYear", section.getDisplayType() == DisplayType.ONE_FILE_PER_YEAR);

        if (section.getReportType() == PERIOD_REPORT) {
            params.put("yearToPeriod", generatePeriodFromYear(reportFiles));
        }

        String sectionHtml;

        if (reportFiles.isEmpty()) {
            sectionHtml = constructMainSectionAttributes(params) + separate() + constructEmptyReport(params) +
                          separate() + constructEndingSectionArea();
        } else if (reportFiles.size() <= moreReportsSize) {
            sectionHtml = constructMainSectionAttributes(params) + separate() + constructReports(params) +
                          separate() + constructEndingSectionArea();
        } else {
            sectionHtml = constructMainSectionAttributes(params) + separate() + constructReports(params) +
                          separate() + constructMoreReports(params) + separate() + constructEndingSectionArea();
        }
        return sectionHtml.getBytes();
    }

    private Map<String, String> generatePeriodFromYear(List<ReportFile> reportFiles) {
        return reportFiles.stream()
                .map(ReportFile::getApplicableForYear)
                .collect(Collectors.toMap(
                    Object::toString,
                    this::calculatePeriod
                ));
    }

    private String calculatePeriod(Integer year) {
        if (year == null) {
            return null;
        }

        int offset = 3;
        int startYear = year - offset - 1;
        int endYear = year - offset;
        LocalDate startDate = LocalDate.of(startYear, 5, 1);
        LocalDate endDate = LocalDate.of(endYear, 4, 30);

        if (year == 2025) {
            startDate = LocalDate.of(startYear, 1, 1);
        }

        return FORMATTER.format(startDate) + "–" + FORMATTER.format(endDate);
    }

    /**
     * Creates a string representation of a Freemarker template.
     *
     * @param template the .ftl template.
     * @param params the parameters.
     * @return a string representation of the template.
     */
    public String createFromTemplate(String template, Map<String, Object> params) {
        try {
            Template t = freemarkerConfiguration.getTemplate(template);
            return FreeMarkerTemplateUtils.processTemplateIntoString(t, params);
        } catch (TemplateException | IOException exception) {
            throw new UkEtsPublicationApiException(ERROR_PROCESSING_TEMPLATE, exception);
        }
    }


    private String constructMainSectionAttributes(Map<String, Object> params) {
        return createFromTemplate(MAIN_SECTION_TEMPLATE, params);
    }

    private String constructEmptyReport(Map<String, Object> params) {
        return createFromTemplate(EMPTY_REPORT_TEMPLATE, params);
    }

    private String constructReports(Map<String, Object> params) {
        return createFromTemplate(REPORTS_SECTION_TEMPLATE, params);
    }

    private String constructMoreReports(Map<String, Object> params) {
        return createFromTemplate(MORE_REPORTS_AREA_TEMPLATE, params);
    }

    private String constructEndingSectionArea() {
        return createFromTemplate(ENDING_SECTION_AREA_TEMPLATE, Collections.emptyMap());
    }

    private String separate() {
        return System.lineSeparator();
    }

    private static List[] splitList(List<ReportFile> list, int size) {
        Iterator<List<ReportFile>> itr = Iterables.partition(list, size)
                                                  .iterator();
        return new List[] {
            new ArrayList<>(itr.next()),
            new ArrayList<>(itr.next())
        };
    }

    private static String getSectionPath(Section section) {
        if (section.getSectionType().equals(SectionType.ETS)) {
            return ETS_REPORT_SECTION + section.getDisplayOrder() + "/";
        }
        return KP_REPORT_SECTION + section.getDisplayOrder() + "/";
    }
}
