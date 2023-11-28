package gov.uk.ets.registry.api.transaction.domain.data;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode(of = {"notificationIdentifier"})
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItlNotificationSummary implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = -4192453403381624727L;

    private Long notificationIdentifier;

    private Long quantity;

    private Integer commitPeriod;

    private Date targetDate;

    private String projectNumber;
}
