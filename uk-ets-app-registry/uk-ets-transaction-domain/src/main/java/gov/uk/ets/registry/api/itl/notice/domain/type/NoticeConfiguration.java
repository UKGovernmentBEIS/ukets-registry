package gov.uk.ets.registry.api.itl.notice.domain.type;

import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.ExternalPredefinedAccount;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

/**
 * Encapsulates the configuration of an ITL notificaitoj.
 */
@Builder
@Getter
public class NoticeConfiguration {

    /**
     * The notification code, according to ITL DES technical specifications.
     */
    private Integer code;

    /**
     * The transactions which can fulfil this notification.
     */
    @Singular("transaction")
    private List<TransactionType> transactions;

    /**
     * The acquiring accounts inside the registry.
     */
    @Singular("account")
    private List<AccountType> acquiringAccount;

    /**
     * The allowed units.
     */
    @Singular("unit")
    private List<UnitType> unitTypes;

    /**
     * The external predefined account.
     */
    private ExternalPredefinedAccount externalPredefinedAccount;

    /**
     * The acquiring account mode.
     */
    private NoticeAcquiringAccountMode mode;

    /**
     * Whether this notification is out of scope for UK ETS.
     */
    private boolean outOfScope;

    /**
     * Whether a transaction initiated by UK ETS is required to fulfil this ITL notification.
     */
    private boolean transactionNeededForFulfilment;

}
