package gov.uk.ets.registry.api.transaction.checks;

import gov.uk.ets.registry.api.transaction.common.GeneratorService;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.data.ItlNotificationSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import gov.uk.ets.registry.api.transaction.service.AccountHoldingService;
import gov.uk.ets.registry.api.transaction.service.LevelService;
import gov.uk.ets.registry.api.transaction.service.TransactionAccountService;
import gov.uk.ets.registry.api.transaction.service.TransactionProposalService;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Parent class for business checks.
 */
public abstract class ParentBusinessCheck implements BusinessCheck {

    /**
     * Account service for transactions.
     */
    @Autowired
    protected TransactionAccountService transactionAccountService;

    /**
     * The generator service.
     */
    @Autowired
    protected GeneratorService generatorService;

    /**
     * The transaction repository.
     */
    @Autowired
    protected TransactionRepository transactionRepository;
    
    /**
     * The level service.
     */
    @Autowired
    protected LevelService levelService;

    /**
     * Service for account holdings.
     */
    @Autowired
    protected AccountHoldingService accountHoldingService;

    /**
     * Service for transaction proposal.
     */
    @Autowired
    protected TransactionProposalService transactionProposalService;

    /**
     * The groups where this check belongs to.
     */
    private EnumSet<BusinessCheckGroup> groups = EnumSet.noneOf(BusinessCheckGroup.class);

    /**
     * Constructor.
     */
    protected ParentBusinessCheck() {
        BusinessCheckGrouping[] grouping = this.getClass().getAnnotationsByType(BusinessCheckGrouping.class);
        if (grouping != null && grouping.length > 0) {
            groups.addAll(Arrays.asList(grouping[0].groups()));
        }
    }

    /**
     * Adds an error to the business context.
     * The error code is dynamically retrieved by the qualifier.
     *
     * @param context The business context.
     * @param message The message
     */
    protected void addError(BusinessCheckContext context, String message) {
        Service annotation = this.getClass().getAnnotation(Service.class);
        String value = annotation.value().replace("check", "");
        Integer errorNumber = NumberUtils.createInteger(value);
        if (context.getTransaction() != null && Constants.isInboundTransaction(context.getTransaction())) {
            errorNumber = getITLErrorNumber();
            message = getITLErrorMessage();
        }
        context.addError(errorNumber, message);
        LogManager.getLogger(this.getClass())
            .info("Business check error {}: {}. {}", errorNumber, message, context.getTransaction());
    }

    /**
     * Returns the acquiring account.
     *
     * @param context The business check context.
     * @return an account.
     */
    protected AccountSummary getAcquiringAccount(BusinessCheckContext context) {
        AccountSummary account = context.get(BusinessCheckContext.ACQUIRING_ACCOUNT, AccountSummary.class);
        if (account == null) {
            TransactionSummary transaction = context.getTransaction();
            account = transactionAccountService.populateAcquiringAccount(transaction);
            context.store(BusinessCheckContext.ACQUIRING_ACCOUNT, account);
        }
        return account;
    }

    /**
     * Returns the transferring account.
     *
     * @param context The business check context.
     * @return an account.
     */
    protected AccountSummary getTransferringAccount(BusinessCheckContext context) {
        AccountSummary account = context.get(BusinessCheckContext.TRANSFERRING_ACCOUNT, AccountSummary.class);

        if (account == null) {
            TransactionSummary transaction = context.getTransaction();
            account = transactionAccountService.populateTransferringAccount(transaction);
            context.store(BusinessCheckContext.TRANSFERRING_ACCOUNT, account);
        }
        return account;
    }

    /**
     * Returns the to Be Replaced units account.
     *
     * @param context The business check context.
     * @return an account.
     */
    protected Optional<AccountSummary> getToBeReplacedUnitsAccount(BusinessCheckContext context) {
        AccountSummary account = context.get(BusinessCheckContext.TO_BE_REPLACED_UNITS_ACCOUNT, AccountSummary.class);

        if (account == null) {
            TransactionSummary transaction = context.getTransaction();
            Optional<AccountSummary> accountOptional = transactionAccountService.populateToBeReplacedUnitsAccount(transaction);
            if(accountOptional.isPresent()) {
                account = accountOptional.get();
                context.store(BusinessCheckContext.TO_BE_REPLACED_UNITS_ACCOUNT, account);            	
            }
            return accountOptional;
        }
        return Optional.empty();
    }
    
    /**
     * Returns the ITL Notice.
     *
     * @param context The business check context.
     * @return an account.
     */
    protected ItlNotificationSummary getITLNotice(BusinessCheckContext context) {
    	ItlNotificationSummary notice = context.get(BusinessCheckContext.ITL_NOTICE, ItlNotificationSummary.class);

        if (notice == null) {
            TransactionSummary transaction = context.getTransaction();
            notice = transaction.getItlNotification();
            context.store(BusinessCheckContext.ITL_NOTICE, notice);
        }
        return notice;
    }  
    
    
    /**
     * Check digits are only applicable to registry accounts.
     *
     * @param accountSummary the accountSummary
     * @return true if check digits are only applicable to registry accounts
     */
    protected boolean hasInvalidCheckDigits(AccountSummary accountSummary) {
    	if(!accountSummary.belongsToRegistry()) {
    		return false;
    	}
        Integer checkDigits = accountSummary.getCheckDigits();
        return checkDigits == null ||
            !generatorService.validateCheckDigits(accountSummary.getKyotoAccountType().getCode(),
                accountSummary.getIdentifier(),
                accountSummary.getCommitmentPeriod(),
                checkDigits);
    }

    /**
     * Utility method to determine if the type of the acquiring account is not allowed to hold the transaction blocks.
     *
     * @param context the context
     * @param blocks  the blocks
     * @return true if the type of the acquiring account is not allowed to hold the transaction blocks.
     */
    protected boolean acquiringAccountIsNotAllowedToHoldTransactionBlocks(BusinessCheckContext context,
                                                                          List<TransactionBlockSummary> blocks) {
        if (!CollectionUtils.isEmpty(blocks)) {
            AccountSummary acquiringAccount = getAcquiringAccount(context);
            if (acquiringAccount != null) {
                AccountType accountType = acquiringAccount.getType();
                if (Boolean.TRUE.equals(accountType.getKyoto())) {
                    return blocks.stream().anyMatch(bl -> !bl.getType().isKyoto());
                } else {
                    return blocks.stream().anyMatch(bl -> bl.getType().isKyoto());
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean belongsToGroup(BusinessCheckGroup businessCheckGroup) {
        return businessCheckGroup == null || groups.contains(businessCheckGroup);
    }

    /**
     * Overrides the error number for compliance with ITL DES specifications.
     * Applies to checks involved during inbound Kyoto transfers.
     * @return an error number.
     */
    protected Integer getITLErrorNumber() {
        return null;
    }

    /**
     * Overrides the error message for compliance with ITL DES specifications.
     * Applies to checks involved during inbound Kyoto transfers.
     * @return an error number.
     */
    protected String getITLErrorMessage() {
        return null;
    }

}
