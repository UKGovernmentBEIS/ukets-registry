package gov.uk.ets.registry.api.account.shared;

public class AccountPropertyPath {

    private AccountPropertyPath() {

    }
    
    /**
     * The account identifier entity property path.
     */
    public static final String ACCOUNT_FULL_IDENTIFIER = "account.fullIdentifier";
    
    /**
     * The account name.
     */
    public static final String ACCOUNT_NAME = "account.accountName";
    
    /**
     * The operator identifier.
     */
    public static final String ACCOUNT_OPERATOR_IDENTIFIER = "account.compliantEntity.identifier";
    
    /*
     * The account type label.
     */
    public static final String ACCOUNT_TYPE_LABEL = "account.accountType";
    
    /**
     * The account holder name property path.
     */
    public static final String ACCOUNT_HOLDER_NAME = "account.holder.name";
    
    /**
     * The Account status property path.
     */
    public static final String ACCOUNT_STATUS = "account.status";
    
    /**
     * The Compliance status property path.
     */
    public static final String ACCOUNT_COMPLIANCE_STATUS = "accountMetrics.dynamicComplianceStatus";
    
    /**
     * The account Surrender Balance property path.
     */
    public static final String ACCOUNT_SURRENDER_BALANCE = "accountMetrics.surrenderBalance";
  
    /**
     * The account Balance property path.
     */
    public static final String ACCOUNT_BALANCE = "account.balance";
}
