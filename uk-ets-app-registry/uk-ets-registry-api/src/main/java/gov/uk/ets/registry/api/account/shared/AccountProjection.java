package gov.uk.ets.registry.api.account.shared;

import com.querydsl.core.annotations.QueryProjection;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.common.search.SearchResult;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountProjection implements SearchResult {

    private Long accountId;
    private Long accountHolderId; // 2 became 1
    private String accountName;
    private AccountStatus accountStatus;
    private String typeLabel;
    private Boolean approvalSecondArRequired;
    private Long balance;
    private Boolean billingAddressSameAsAccountHolderAddress;
    private Integer checkDigits;
    private Integer commitmentPeriodCode;
    private ComplianceStatus complianceStatus;
    private Long compliantEntityId;
    private Long contactId;
    private String fullIdentifier;
    private Long identifier;
    private KyotoAccountType kyotoAccountType;
    private Date openingDate;
    private RegistryAccountType registryAccountType;
    private String registryCode;
    private Status requestStatus;
    private Boolean transfersToAccountsNotOnTheTrustedListAreAllowed;
    private UnitType unitType;
    private String accountHolderBirthCountry;
    private Date accountHolderBirthDate;
    private Long accountHolderContactId;
    private String accountHolderFirstName;
    private Long accountHolderIdentifier;
    private String accountHolderLastName;
    private String accountHolderName;
    private String accountHolderNoRegJustification;
    private String accountHolderRegistrationNumber;
    private AccountHolderType accountHolderType;
    private RegulatorType regulator;

    /**
     *
     * @param accountId
     * @param accountHolderId
     * @param accountName
     * @param accountStatus
     * @param typeLabel
     * @param approvalSecondArRequired
     * @param balance
     * @param billingAddressSameAsAccountHolderAddress
     * @param checkDigits
     * @param commitmentPeriodCode
     * @param complianceStatus
     * @param compliantEntityId
     * @param contactId
     * @param fullIdentifier
     * @param identifier
     * @param kyotoAccountType
     * @param openingDate
     * @param registryAccountType
     * @param registryCode
     * @param requestStatus
     * @param transfersToAccountsNotOnTheTrustedListAreAllowed
     * @param unitType
     * @param accountHolderBirthCountry
     * @param accountHolderBirthDate
     * @param accountHolderContactId
     * @param accountHolderFirstName
     * @param accountHolderIdentifier
     * @param accountHolderLastName
     * @param accountHolderName
     * @param accountHolderNoRegJustification
     * @param accountHolderRegistrationNumber
     * @param accountHolderType
     * @param regulator
     */
    @QueryProjection
    public AccountProjection(Long accountId, Long accountHolderId, String accountName,
                             AccountStatus accountStatus,
                             String typeLabel, Boolean approvalSecondArRequired, Long balance,
                             Boolean billingAddressSameAsAccountHolderAddress, Integer checkDigits,
                             Integer commitmentPeriodCode,
                             ComplianceStatus complianceStatus, Long compliantEntityId, Long contactId,
                             String fullIdentifier, Long identifier,
                             KyotoAccountType kyotoAccountType, Date openingDate,
                             RegistryAccountType registryAccountType, String registryCode,
                             Status requestStatus, Boolean transfersToAccountsNotOnTheTrustedListAreAllowed,
                             UnitType unitType, String accountHolderBirthCountry, Date accountHolderBirthDate,
                             Long accountHolderContactId, String accountHolderFirstName,
                             Long accountHolderIdentifier, String accountHolderLastName, 
                             String accountHolderName, String accountHolderNoRegJustification, 
                             String accountHolderRegistrationNumber, AccountHolderType accountHolderType,
                             RegulatorType regulator) {

        this.accountId = accountId;
        this.accountHolderId = accountHolderId;
        this.accountName = accountName;
        this.accountStatus = accountStatus;
        this.typeLabel = typeLabel;
        this.approvalSecondArRequired = approvalSecondArRequired;
        this.balance = balance;
        this.billingAddressSameAsAccountHolderAddress = billingAddressSameAsAccountHolderAddress;
        this.checkDigits = checkDigits;
        this.commitmentPeriodCode = commitmentPeriodCode;
        this.complianceStatus = complianceStatus;
        this.compliantEntityId = compliantEntityId;
        this.contactId = contactId;
        this.fullIdentifier = fullIdentifier;
        this.identifier = identifier;
        this.kyotoAccountType = kyotoAccountType;
        this.openingDate = openingDate;
        this.registryAccountType = registryAccountType;
        this.registryCode = registryCode;
        this.requestStatus = requestStatus;
        this.transfersToAccountsNotOnTheTrustedListAreAllowed = transfersToAccountsNotOnTheTrustedListAreAllowed;
        this.unitType = unitType;
        this.accountHolderBirthCountry = accountHolderBirthCountry;
        this.accountHolderBirthDate = accountHolderBirthDate;
        this.accountHolderContactId = accountHolderContactId;
        this.accountHolderFirstName = accountHolderFirstName;
        this.accountHolderIdentifier = accountHolderIdentifier;
        this.accountHolderLastName = accountHolderLastName;
        this.accountHolderName = accountHolderName;
        this.accountHolderNoRegJustification = accountHolderNoRegJustification;
        this.accountHolderRegistrationNumber = accountHolderRegistrationNumber;
        this.accountHolderType = accountHolderType;
        this.regulator = regulator;
    }
}
