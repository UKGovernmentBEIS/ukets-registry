package gov.uk.ets.reports.generator.kyotoprotocol.sef.processors.cp1;

import gov.uk.ets.reports.generator.kyotoprotocol.commons.RF2TableFactory;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.util.CerQtyUtil;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.util.TableUtil;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.util.TotalUnitQtyUtil;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.util.UnitQtyUtil;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.AccountType;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Additions;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.AnnualReplacement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.AnnualRetirement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.AnnualTransaction;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.CerQty;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.ExternalTransfer;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SEFSubmission;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.StartingValue;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SubTotal;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Subtractions;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Table1;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Table2A;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Table2B;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Table3;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Table4;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Table5A;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Table5B;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Table5C;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TotalAdditionSubtraction;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TotalUnitQty;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionOrEventType;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionType;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.UnitQty;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.enums.QuantityValueEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kattoulp
 *
 */
public class AggregatorProcessor extends AbstractTransactionProcessor {

    public Map<String, SEFSubmission> aggregate(Map<String, SEFSubmission> sefSubmissions, String aggregatedGroupName) {
        Map<String, SEFSubmission> res = new HashMap<String, SEFSubmission>();
        SEFSubmission aggregatedSubmission = getOrCreateSubmission(res, aggregatedGroupName);
        aggregateTable1(sefSubmissions, aggregatedSubmission);
        aggregateTable2a(sefSubmissions, aggregatedSubmission);
        aggregateTable2b(sefSubmissions, aggregatedSubmission);
        calculateTable2cAggregations(aggregatedSubmission);
        aggregateTable3(sefSubmissions, aggregatedSubmission);
        aggregateTable4(sefSubmissions, aggregatedSubmission);
        aggregateTable5a(sefSubmissions, aggregatedSubmission);
        aggregateTable5b(sefSubmissions, aggregatedSubmission);
        aggregateTable5c(sefSubmissions, aggregatedSubmission);

        fixTable5a(sefSubmissions, aggregatedSubmission);

        return res;
    }



    private void fixTable5a(Map<String, SEFSubmission> sefSubmissions, SEFSubmission aggregatedSubmission) {
        Table5A aggregatedTable5a = aggregatedSubmission.getTable5A();
        final AnnualTransaction annualTransactionToFix = TableUtil.getAnnualTransactionByYear(
                aggregatedTable5a.getAnnualTransactions().getAnnualTransaction(), aggregatedSubmission.getHeader().getReportedYear());
        final SubTotal subtotalToFix = aggregatedTable5a.getAnnualTransactions().getSubTotal();
        final TotalAdditionSubtraction totalToFix = aggregatedTable5a.getTotalAdditionSubtraction();
        for (SEFSubmission reg : sefSubmissions.values()) {
            Table2B regTable = reg.getTable2B();
            for (ExternalTransfer exTr : regTable.getExternalTransfer()) {
                String registryCode = exTr.getRegistry();
                if (belongsToAggregationGroup(registryCode)) {
                    // here fix table5a by reducing the values for the reported year and the total
                    fixAdditionSubtraction(annualTransactionToFix.getAdditions(), annualTransactionToFix.getSubtractions(), exTr);
                    fixAdditionSubtraction(subtotalToFix.getAdditions(), subtotalToFix.getSubtractions(), exTr);
                    fixAdditionSubtraction(totalToFix.getAdditions(), totalToFix.getSubtractions(), exTr);
                }
            }

        }

    }

    /**
     * Method that fixes table 5a
     * @param additionsToFix
     * @param subtractionsToFix
     * @param exTr
     */
    private void fixAdditionSubtraction(Additions additionsToFix, Subtractions subtractionsToFix, ExternalTransfer exTr) {
        for (UnitQty unitQty : exTr.getAdditions().getUnitQty()) {
            UnitQty targetQ = UnitQtyUtil.getUnitQtyByUnitType(additionsToFix.getUnitQty(), unitQty.getType());
            subtract(targetQ, unitQty);
        }
        for (UnitQty unitQty : exTr.getSubtractions().getUnitQty()) {
            UnitQty targetQ = UnitQtyUtil.getUnitQtyByUnitType(subtractionsToFix.getUnitQty(), unitQty.getType());
            subtract(targetQ, unitQty);
        }

    }



    /**
     * @param sefSubmissions
     * @param aggregated
     */
    private void aggregateTable5c(Map<String, SEFSubmission> sefSubmissions, SEFSubmission aggregated) {
        Table5C aggTable = aggregated.getTable5C();
        for (SEFSubmission reg : sefSubmissions.values()) {
            Table5C regTable = reg.getTable5C();
            for (AnnualRetirement regRetirement : regTable.getAnnualRetirements().getAnnualRetirement()) {
                AnnualRetirement aggRetirement = TableUtil.getAnnualRetirement(aggTable.getAnnualRetirements()
                        .getAnnualRetirement(), regRetirement.getYear());
                aggregate(aggRetirement.getUnitQty(), regRetirement.getUnitQty());
            }
            aggregate(aggTable.getTotalUnitQty().getUnitQty(), regTable.getTotalUnitQty().getUnitQty());

        }
    }

    /**
     * @param sefSubmissions
     * @param aggregated
     */
    private void aggregateTable5b(Map<String, SEFSubmission> sefSubmissions, SEFSubmission aggregated) {
        Table5B aggTable = aggregated.getTable5B();
        for (SEFSubmission reg : sefSubmissions.values()) {
            Table5B regTable = reg.getTable5B();
            for (AnnualReplacement regReplacement : regTable.getAnnualReplacements().getAnnualReplacement()) {
                AnnualReplacement aggReplacement = TableUtil.getAnnualReplacementByType(aggTable
                        .getAnnualReplacements().getAnnualReplacement(), regReplacement.getYear());
                aggregateCerList(aggReplacement.getRequirementForReplacement().getCerQty(), regReplacement
                        .getRequirementForReplacement().getCerQty());
                aggregate(aggReplacement.getReplacement().getUnitQty(), regReplacement.getReplacement().getUnitQty());
            }
            aggregateCerList(aggTable.getTotalTxOrEvent().getRequirementForReplacement().getCerQty(), regTable
                    .getTotalTxOrEvent().getRequirementForReplacement().getCerQty());
            aggregate(aggTable.getTotalTxOrEvent().getReplacement().getUnitQty(), regTable.getTotalTxOrEvent()
                    .getReplacement().getUnitQty());
        }
    }

    /**
     * @param sefSubmissions
     * @param aggregated
     */
    private void aggregateTable5a(Map<String, SEFSubmission> sefSubmissions, SEFSubmission aggregated) {
        Table5A aggTable = aggregated.getTable5A();
        for (SEFSubmission reg : sefSubmissions.values()) {
            Table5A regTable = reg.getTable5A();
            for (StartingValue regStartingValue : regTable.getStartingValues().getStartingValue()) {
                StartingValue aggStartingValue = TableUtil.getStartingValueByType(aggTable.getStartingValues(),
                        regStartingValue.getName());
                aggregate(aggStartingValue.getAdditions(), regStartingValue.getAdditions());
                aggregate(aggStartingValue.getSubtractions(), regStartingValue.getSubtractions());
            }
            aggregate(aggTable.getStartingValues().getSubTotal().getAdditions(), regTable.getStartingValues().getSubTotal().getAdditions());
            aggregate(aggTable.getStartingValues().getSubTotal().getSubtractions(), regTable.getStartingValues().getSubTotal().getSubtractions());

            for (AnnualTransaction regAnnual : regTable.getAnnualTransactions().getAnnualTransaction()) {
                AnnualTransaction aggAnnual = TableUtil.getAnnualTransactionByYear(aggTable.getAnnualTransactions()
                        .getAnnualTransaction(), regAnnual.getYear());
                aggregate(aggAnnual.getAdditions(), regAnnual.getAdditions());
                aggregate(aggAnnual.getSubtractions(), regAnnual.getSubtractions());
            }
            aggregate(aggTable.getAnnualTransactions().getSubTotal().getAdditions(), regTable.getAnnualTransactions().getSubTotal()
                    .getAdditions());
            aggregate(aggTable.getAnnualTransactions().getSubTotal().getSubtractions(), regTable.getAnnualTransactions().getSubTotal()
                    .getSubtractions());

            aggregate(aggTable.getTotalAdditionSubtraction().getAdditions(), regTable.getTotalAdditionSubtraction()
                    .getAdditions());
            aggregate(aggTable.getTotalAdditionSubtraction().getSubtractions(), regTable.getTotalAdditionSubtraction()
                    .getSubtractions());
        }

    }

    /**
     * @param sefSubmissions
     * @param aggregated
     */
    private void aggregateTable3(Map<String, SEFSubmission> sefSubmissions, SEFSubmission aggregated) {
        Table3 aggTable = aggregated.getTable3();
        for (SEFSubmission reg : sefSubmissions.values()) {
            Table3 regTable = reg.getTable3();
            for (TransactionOrEventType regType : regTable.getTransactionOrEventType()) {
                TransactionOrEventType aggType = TableUtil.getTransactionOrEventTypeByType(
                        aggTable.getTransactionOrEventType(), regType.getName());
                aggregateCerList(aggType.getRequirementForReplacement().getCerQty(), regType
                        .getRequirementForReplacement().getCerQty());
                aggregate(aggType.getReplacement().getUnitQty(), regType.getReplacement().getUnitQty());
            }
            aggregateCerList(aggTable.getTotalTxOrEvent().getRequirementForReplacement().getCerQty(), regTable
                    .getTotalTxOrEvent().getRequirementForReplacement().getCerQty());
            aggregate(aggTable.getTotalTxOrEvent().getReplacement().getUnitQty(), regTable.getTotalTxOrEvent()
                    .getReplacement().getUnitQty());
        }

    }

    /**
     * @param aggregatedSubmission
     */
    private void calculateTable2cAggregations(SEFSubmission aggregatedSubmission) {
        aggregate(aggregatedSubmission.getTable2C().getAdditions(), aggregatedSubmission.getTable2A().getSubTotal()
                .getAdditions());
        aggregate(aggregatedSubmission.getTable2C().getAdditions(), aggregatedSubmission.getTable2B().getSubTotal()
                .getAdditions());

        aggregate(aggregatedSubmission.getTable2C().getSubtractions(), aggregatedSubmission.getTable2A().getSubTotal()
                .getSubtractions());
        aggregate(aggregatedSubmission.getTable2C().getSubtractions(), aggregatedSubmission.getTable2B().getSubTotal()
                .getSubtractions());
    }

    /**
     * @param sefSubmissions
     * @param aggregated
     * @return
     */
    private SEFSubmission aggregateTable1(Map<String, SEFSubmission> sefSubmissions, SEFSubmission aggregated) {
        Table1 aggTable = aggregated.getTable1();
        for (SEFSubmission reg : sefSubmissions.values()) {
            Table1 regTable = reg.getTable1();
            for (AccountType type : regTable.getAccountType()) {
                for (UnitQty qty : type.getUnitQty()) {
                    UnitQty aggQTy = TableUtil.getUnitQty(aggTable, type.getName(), qty.getType());
                    aggregate(aggQTy, qty);
                }
            }
            aggregate(aggTable.getTotalUnitQty(), regTable.getTotalUnitQty());
        }

        return aggregated;
    }

    /**
     * @param sefSubmissions
     * @param aggregated
     * @return
     */
    private SEFSubmission aggregateTable4(Map<String, SEFSubmission> sefSubmissions, SEFSubmission aggregated) {
        Table4 aggTable = aggregated.getTable4();
        for (SEFSubmission reg : sefSubmissions.values()) {
            Table4 regTable = reg.getTable4();
            for (AccountType type : regTable.getAccountType()) {
                for (UnitQty qty : type.getUnitQty()) {
                    UnitQty aggQTy = TableUtil.getUnitQty(aggTable, type.getName(), qty.getType());
                    aggregate(aggQTy, qty);
                }
            }
            aggregate(aggTable.getTotalUnitQty(), regTable.getTotalUnitQty());
        }

        return aggregated;
    }

    /**
     * @param sefSubmissions
     * @param aggregated
     * @return
     */
    private SEFSubmission aggregateTable2a(Map<String, SEFSubmission> sefSubmissions, SEFSubmission aggregated) {
        Table2A aggTable = aggregated.getTable2A();
        for (SEFSubmission reg : sefSubmissions.values()) {
            Table2A regTable = reg.getTable2A();
            for (TransactionType regType : regTable.getTransactionType()) {
                TransactionType aggType = TableUtil.getTransactionTypeByType(aggTable.getTransactionType(),
                        regType.getName());
                // aggregate additions
                aggregate(aggType.getAdditions(), regType.getAdditions());
                // aggregate subtractions
                aggregate(aggType.getSubtractions(), regType.getSubtractions());
            }
            // aggregate additions
            aggregate(aggTable.getSubTotal().getAdditions(), regTable.getSubTotal().getAdditions());
            // aggregate subtractions
            aggregate(aggTable.getSubTotal().getSubtractions(), regTable.getSubTotal().getSubtractions());

            //aggregateRetirement
            aggregate(aggTable.getRetirement().getUnitQty(), regTable.getRetirement().getUnitQty());

        }
        return aggregated;
    }


    /**
     * @param sefSubmissions
     * @param aggregated
     * @return
     */
    private SEFSubmission aggregateTable2b(Map<String, SEFSubmission> sefSubmissions, SEFSubmission aggregated) {
        Table2B aggTable = aggregated.getTable2B();
        for (SEFSubmission reg : sefSubmissions.values()) {
            Table2B regTable = reg.getTable2B();
            for (ExternalTransfer exTr : regTable.getExternalTransfer()) {
                String registryCode = exTr.getRegistry();
                if (!belongsToAggregationGroup(registryCode)) {
                    ExternalTransfer aggExtTransfer = TableUtil.getExternalTransferByRegistry(
                            aggTable.getExternalTransfer(), registryCode);
                    if (aggExtTransfer == null) {
                        aggExtTransfer = RF2TableFactory.createExternalTransfer(registryCode);
                        aggTable.getExternalTransfer().add(aggExtTransfer);
                    }
                    // aggregate additions
                    aggregate(aggExtTransfer.getAdditions(), exTr.getAdditions());
                    aggregate(aggTable.getSubTotal().getAdditions(), exTr.getAdditions());
                    // aggregate subtractions
                    aggregate(aggExtTransfer.getSubtractions(), exTr.getSubtractions());
                    aggregate(aggTable.getSubTotal().getSubtractions(), exTr.getSubtractions());
                }
            }

        }

        return aggregated;
    }


    /**
     * @param aggregatedSubtractions
     * @param subtractions
     */
    private void aggregate(Subtractions aggregatedSubtractions, Subtractions subtractions) {
        aggregate(aggregatedSubtractions.getUnitQty(), subtractions.getUnitQty());
    }

    /**
     * @param aggregatedAdditions
     * @param additions
     */
    private void aggregate(Additions aggregatedAdditions, Additions additions) {
        aggregate(aggregatedAdditions.getUnitQty(), additions.getUnitQty());
    }

    /**
     * @param aggregationQtys
     * @param registryQtys
     */
    private void aggregate(List<UnitQty> aggregationQtys, List<UnitQty> registryQtys) {
        for (UnitQty qty : registryQtys) {
            UnitQty aggQTy = UnitQtyUtil.getUnitQtyByUnitType(aggregationQtys, qty.getType());
            aggregate(aggQTy, qty);
        }
    }

    private void aggregateCerList(List<CerQty> aggCerQty, List<CerQty> regCerQty) {
        for (CerQty qty : regCerQty) {
            CerQty aggQTy = CerQtyUtil.getCerQtyByCerType(aggCerQty, qty.getType());
            aggregate(aggQTy, qty);
        }

    }

    /**
     * @param registryCode
     * @return
     */
    private boolean belongsToAggregationGroup(String registryCode) {
        return ConfigLoader.getConfigLoader().getReportedRegistries().contains(registryCode);
    }

    /**
     * Aggregates the reg total object to the agg total object
     *
     * @param aggTotal
     * @param regTotal
     */
    private void aggregate(TotalUnitQty aggTotal, TotalUnitQty regTotal) {
        for (UnitQty qty : regTotal.getUnitQty()) {
            UnitQty aggQTy = TotalUnitQtyUtil.getUnitQtyByUnitType(aggTotal, qty.getType());
            aggregate(aggQTy, qty);
        }
    }

    /**
     * @param qty
     * @param toAdd
     * @return
     */
    private UnitQty aggregate(UnitQty qty, UnitQty toAdd) {
        if (qty.getType() != toAdd.getType()) {
            throw new IllegalArgumentException("Invalid unit quantities to aggregate");
        }

        if (toAdd.getValue().equals(QuantityValueEnum.NO.name())
                || toAdd.getValue().equals(QuantityValueEnum.NA.name())) {
            return qty;
        }

        updateUnitQty(Long.valueOf(toAdd.getValue()), qty);
        return qty;
    }

    /**
     * Subtracts a unit qty from the target qty
     *
     * @param qty
     * @param toSubtract
     * @return
     */
    private UnitQty subtract(UnitQty qty, UnitQty toSubtract) {
        if (qty.getType() != toSubtract.getType()) {
            throw new IllegalArgumentException("Invalid unit quantities for subtraction");
        }

        if (toSubtract.getValue().equals(QuantityValueEnum.NO.name())
                || toSubtract.getValue().equals(QuantityValueEnum.NA.name())) {
            return qty;
        }

        updateUnitQty(-Long.valueOf(toSubtract.getValue()), qty);
        return qty;
    }

    private CerQty aggregate(CerQty aggQTy, CerQty toAdd) {
        if (aggQTy.getType() != toAdd.getType()) {
            throw new IllegalArgumentException("Invalid cer quantities to aggregate");
        }

        if (toAdd.getValue().equals(QuantityValueEnum.NO.name())
                || toAdd.getValue().equals(QuantityValueEnum.NA.name())) {
            return aggQTy;
        }

        updateCerQty(Long.valueOf(toAdd.getValue()), aggQTy);
        return aggQTy;
    }
}
