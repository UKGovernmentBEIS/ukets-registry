package gov.uk.ets.reports.generator.kyotoprotocol;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class KyotoReportOutcome {

    private byte[] content;
    private String fileName;
    private KyotoReportContentType contentType;

    @Getter
    public enum KyotoReportContentType {
        TEXT_XML("text/xml"), APPLICATION_VND_MS_EXCEL(
            "application/vnd.ms-excel");

        private final String value;

        KyotoReportContentType(String value) {
            this.value = value;
        }
    };
}
