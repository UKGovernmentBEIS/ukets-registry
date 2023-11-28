/**
 * 
 */
package gov.uk.ets.registry.api.transaction.shared;

import lombok.Getter;

/**
 * @author P35036
 *
 */
public enum TransactionSearchAliases {

    RUNNING_BALANCE_QUANTITY("runningBalanceQuantity"),
    RUNNING_BALANCE_UNIT_TYPE("runningBalanceUnitType");
    
    @Getter
    private final String alias;
    
    TransactionSearchAliases(String name) {
        this.alias = name;
    }
    
    
}
