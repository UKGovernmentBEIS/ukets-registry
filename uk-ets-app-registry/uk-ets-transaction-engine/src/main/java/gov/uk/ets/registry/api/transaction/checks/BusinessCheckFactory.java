package gov.uk.ets.registry.api.transaction.checks;

import static java.lang.String.format;

import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.stereotype.Service;

/**
 * The check factory.
 */
@Service
public class BusinessCheckFactory {

    /**
     * The bean factory.
     */
    private final BeanFactory context;

    /**
     * Bean responsible for the mapping of business-checks.properties file
     */
    private final BusinessChecksConfiguration businessChecksConfiguration;

    /**
     * Maps a transaction type with the check codes.
     */
    private EnumMap<TransactionType, List<BusinessCheck>> checksPerTransaction = new EnumMap<>(TransactionType.class);

    /**
     * Holds the business checks which are executed regardless the transaction type.
     */
    private List<BusinessCheck> commonChecks = new ArrayList<>();

    /**
     * The {@link BusinessCheckFactory} constructor.
     * @param context the context
     * @param businessChecksConfiguration the businessChecksConfiguration
     */
    public BusinessCheckFactory(BeanFactory context, BusinessChecksConfiguration businessChecksConfiguration) {
        this.context = context;
        this.businessChecksConfiguration = businessChecksConfiguration;
        loadChecks();
    }

    /**
     * Loads the checks.
     */
    private void loadChecks() {

        Map<String,String[]> specificTransactionChecks = businessChecksConfiguration.getChecksMap();

        // Load common checks for all transactions
        String[] array = businessChecksConfiguration.getCommonChecks();
        for (String checkNumber : array) {
            commonChecks.add(BeanFactoryAnnotationUtils.qualifiedBeanOfType(context, BusinessCheck.class, "check" + checkNumber));
        }

        // Load specific transaction checks
        for (TransactionType type : TransactionType.values()) {
            if (specificTransactionChecks.containsKey(type.toString())) {
                array = specificTransactionChecks.get(type.toString());
                List<BusinessCheck> checks = new ArrayList<>();
                checksPerTransaction.put(type, checks);
                for (String checkIdentifier : array) {
                    checks.add(BeanFactoryAnnotationUtils.qualifiedBeanOfType(context, BusinessCheck.class, "check" + checkIdentifier));
                }
            }
        }
    }

    /**
     * Returns the common checks for all transactions.
     * @return some checks
     */
    public List<BusinessCheck> getCommonChecks() {
        return commonChecks;
    }

    /**
     * Returns the checks for the provided transaction type.
     * @param type The transaction type.
     * @return some checks
     */
    public List<BusinessCheck> getChecksForTransactionType(TransactionType type) {
        List<BusinessCheck> result = checksPerTransaction.get(type);
        if (result == null) {
            throw new IllegalArgumentException(format("The checks for transaction type %s have not been defined yet.", type));
        }
        return result;
    }

}
