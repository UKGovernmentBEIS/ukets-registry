package uk.gov.ets.transaction.log.helper;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.ets.transaction.log.domain.AccountBasicInfo;
import uk.gov.ets.transaction.log.domain.Transaction;
import uk.gov.ets.transaction.log.domain.UnitBlock;
import uk.gov.ets.transaction.log.domain.type.TransactionStatus;
import uk.gov.ets.transaction.log.domain.type.TransactionType;
import uk.gov.ets.transaction.log.domain.type.UnitType;
import uk.gov.ets.transaction.log.messaging.types.ReconciliationEntrySummary;

@ExtendWith(SpringExtension.class)
@DisplayNameGeneration(BaseTest.ReplaceCamelCase.class)
public abstract class BaseTest {
    public static class ReplaceCamelCase extends DisplayNameGenerator.Standard {
        @Override
        public String generateDisplayNameForClass(Class<?> testClass) {
            return replaceCamelCase(super.generateDisplayNameForClass(testClass));
        }

        @Override
        public String generateDisplayNameForNestedClass(Class<?> nestedClass) {
            return replaceCamelCase(super.generateDisplayNameForNestedClass(nestedClass));
        }

        @Override
        public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
            return this.replaceCamelCase(testMethod.getName()) +
                DisplayNameGenerator.parameterTypesAsString(testMethod);
        }

        String replaceCamelCase(String camelCase) {
            StringBuilder result = new StringBuilder();
            result.append(camelCase.charAt(0));
            for (int i = 1; i < camelCase.length(); i++) {
                if (Character.isUpperCase(camelCase.charAt(i))) {
                    result.append(' ');
                    result.append(Character.toLowerCase(camelCase.charAt(i)));
                } else {
                    result.append(camelCase.charAt(i));
                }
            }
            return result.toString();
        }
    }

    public Date convertToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    protected Transaction createTransactionWithIdStatusAndAccountsAtDateStarted(String id,
                                                                                TransactionStatus status,
                                                                                LocalDateTime started) {
        Transaction transaction = new Transaction();
        transaction.setIdentifier(id);
        transaction.setStatus(status);
        transaction.setType(TransactionType.TransferAllowances);
        transaction.setUnitType(UnitType.ALLOWANCE);
        transaction.setStarted(convertToDate(started));
        return transaction;
    }

    protected Transaction createTransactionWithIdStatusAndAccountsAtDateStarted(String id,
                                                                                TransactionStatus status,
                                                                                LocalDateTime started,
                                                                                AccountBasicInfo acquiringAccount,
                                                                                AccountBasicInfo transferringAccount) {
        Transaction tr =
            createTransactionWithIdStatusAndAccountsAtDateStarted(id, status, started);
        tr.setAcquiringAccount(acquiringAccount);
        tr.setTransferringAccount(transferringAccount);
        return tr;
    }

    protected ReconciliationEntrySummary createReconciliationEntrySummary(Long accountIdentifier, UnitType unitType,
                                                                          long total) {
        ReconciliationEntrySummary e = new ReconciliationEntrySummary();
        e.setAccountIdentifier(accountIdentifier);
        e.setUnitType(unitType);
        e.setTotal(total);
        return e;
    }

    protected UnitBlock createUnitBlockForAccountWithBlocks(Long accountIdentifier, Long start, Long end) {
        UnitBlock block = new UnitBlock();
        block.setStartBlock(start);
        block.setEndBlock(end);
        block.setAccountIdentifier(accountIdentifier);
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(2020, 7, 8);
        block.setAcquisitionDate(cal.getTime());
        block.setLastModifiedDate(cal.getTime());
        block.setType(UnitType.ALLOWANCE);
        block.setYear(2020);
        return block;
    }
}
