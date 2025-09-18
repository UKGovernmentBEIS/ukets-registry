package gov.uk.ets.registry.api.notification;

import com.google.common.collect.ImmutableMap;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import java.util.Map;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AccountManagementNotificationProperties {

    private static final String AUTHORISED_REPRESENTATIVES = "authorised representatives";
    private static final String TRUSTED_ACCOUNT_LIST = "trusted account list";
    private static final String TRANSACTION_RULES = "transaction rules";
    private static final String ACCOUNT_HOLDER = "account holder";
    private static final String ACCOUNT_HOLDER_CONTACT = "account holder contact";

    /**
     * Private constructor.
     */
    private AccountManagementNotificationProperties() {
    }

    @NotBlank
    private String subject;

    // Maps request types related to account management with request reason texts
    public static final Map<RequestType, String> ACCOUNT_MANAGEMENT_REQUEST_TYPES =
        ImmutableMap.<RequestType, String>builder()
        .put(RequestType.ADD_TRUSTED_ACCOUNT_REQUEST, TRUSTED_ACCOUNT_LIST)
        .put(RequestType.DELETE_TRUSTED_ACCOUNT_REQUEST, TRUSTED_ACCOUNT_LIST)
        .put(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST, AUTHORISED_REPRESENTATIVES)
        .put(RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST, AUTHORISED_REPRESENTATIVES)
        .put(RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST, AUTHORISED_REPRESENTATIVES)
        .put(RequestType.AUTHORIZED_REPRESENTATIVE_SUSPEND_REQUEST, AUTHORISED_REPRESENTATIVES)
        .put(RequestType.AUTHORIZED_REPRESENTATIVE_RESTORE_REQUEST, AUTHORISED_REPRESENTATIVES)
        .put(RequestType.AUTHORIZED_REPRESENTATIVE_UPDATE_ACCESS_RIGHTS_REQUEST, AUTHORISED_REPRESENTATIVES)
        .put(RequestType.TRANSACTION_RULES_UPDATE_REQUEST, TRANSACTION_RULES)
        .put(RequestType.ACCOUNT_HOLDER_UPDATE_DETAILS, ACCOUNT_HOLDER)
        .put(RequestType.ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS, ACCOUNT_HOLDER_CONTACT)
        .put(RequestType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_UPDATE, ACCOUNT_HOLDER_CONTACT)
        .put(RequestType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_DELETE, ACCOUNT_HOLDER_CONTACT)
        .put(RequestType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_ADD, ACCOUNT_HOLDER_CONTACT)
        .build();
}
