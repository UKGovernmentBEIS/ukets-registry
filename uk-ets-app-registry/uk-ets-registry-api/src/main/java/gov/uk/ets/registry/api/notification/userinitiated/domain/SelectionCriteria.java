package gov.uk.ets.registry.api.notification.userinitiated.domain;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelectionCriteria {

    private List<String> accountTypes;

    private List<AccountStatus> accountStatuses;

    private List<UserStatus> userStatuses;

    private List<AccountAccessState> accountAccessStates;

    private List<ComplianceStatus> complianceStatuses;
}
