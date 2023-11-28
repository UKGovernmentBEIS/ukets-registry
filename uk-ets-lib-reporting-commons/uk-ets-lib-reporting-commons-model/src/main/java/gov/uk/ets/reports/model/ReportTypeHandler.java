package gov.uk.ets.reports.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import gov.uk.ets.reports.model.criteria.AccountSearchCriteria;
import gov.uk.ets.reports.model.criteria.ReportCriteria;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Common parent for classes that refer to report criteria. Used both by API DTOs and Kafka messages.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public abstract class ReportTypeHandler {

    @NotNull
    private ReportType type;

    /**
     * Maps every ReportType with a specific criteria class, so it can be correctly deserialized.
     *
     * @deprecated we are not going to use criteria, the whole query is passed in ReportQueryInfo instead.
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type",
        defaultImpl = ReportType.class)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = AccountSearchCriteria.class, name = "R0001")
    })
    @Valid
    // In case criteria object is present it should be validated but it is not mandatory (case of standard reports)
    @Deprecated
    private ReportCriteria criteria;

    // In case ReportQueryInfo is present it should be validated but it is not mandatory (case of standard reports)
    @Valid
    private ReportQueryInfo reportQueryInfo;
}
