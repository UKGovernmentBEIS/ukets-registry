package gov.uk.ets.reports.generator.kyotoprotocol.sef.processors.cp2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.uk.ets.reports.generator.kyotoprotocol.commons.RF2TableFactory;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.enums.QuantityValueEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.util.CerQtyUtil;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.util.TableUtil;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.util.TotalUnitQtyUtil;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.util.UnitQtyUtil;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.AccountType;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Additions;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.CerQty;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.ExternalTransfer;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.StartingValue;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Subtractions;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TotalAdditionSubtraction;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TotalUnitQty;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionType;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.UnitQty;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AdaptationFundType;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AnnualReplacement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AnnualRetirement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AnnualTransaction;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AnnualTransactionPPSR;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.PPSRTransfer;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Retirement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table1;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table2A;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table2ARetirement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table2B;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table2C;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table2D;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table3;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table4;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table5A;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table5B;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table5C;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table5D;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table5E;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.TransactionOrEventType;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.ConfigLoader;

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
		aggregateTable2aRetirement(sefSubmissions, aggregatedSubmission);
		aggregateTable2b(sefSubmissions, aggregatedSubmission);
		aggregateTable2c(sefSubmissions, aggregatedSubmission);
		aggregateTable2d(sefSubmissions, aggregatedSubmission);
		calculateTable2eAggregations(aggregatedSubmission);
		aggregateTable3(sefSubmissions, aggregatedSubmission);
		aggregateTable4(sefSubmissions, aggregatedSubmission);
		aggregateTable5a(sefSubmissions, aggregatedSubmission);
		aggregateTable5b(sefSubmissions, aggregatedSubmission);
		aggregateTable5c(sefSubmissions, aggregatedSubmission);
		aggregateTable5d(sefSubmissions, aggregatedSubmission);
		aggregateTable5e(sefSubmissions, aggregatedSubmission);

		fixTable5b(sefSubmissions, aggregatedSubmission);
		fixTable5c(sefSubmissions, aggregatedSubmission);
		
		return res;
	}
	
	private void fixTable5b(Map<String, SEFSubmission> sefSubmissions, SEFSubmission aggregatedSubmission) {
		Table5B aggregatedTable5b = aggregatedSubmission.getTable5B();
		final AnnualTransaction annualTransactionToFix = TableUtil.getSef2AnnualTransactionByYear(
				aggregatedTable5b.getAnnualTransactions().getAnnualTransaction(), aggregatedSubmission.getHeader().getReportedYear());		
		final TotalAdditionSubtraction totalToFix = aggregatedTable5b.getTotalAdditionSubtraction();
		for (SEFSubmission reg : sefSubmissions.values()) {
			Table2B regTable = reg.getTable2B();
			for (ExternalTransfer exTr : regTable.getExternalTransfer()) {
				String registryCode = exTr.getRegistry();
				if (belongsToAggregationGroup(registryCode)) {					
					// here fix table5a by reducing the values for the reported year and the total					
					fixAdditionSubtraction(annualTransactionToFix.getAdditions(), annualTransactionToFix.getSubtractions(), exTr);									
					fixAdditionSubtraction(totalToFix.getAdditions(), totalToFix.getSubtractions(), exTr);
				} else {
					// do nothing - it is already covered in aggregateTable2b
				}
			}
		}		
	}
	
	private void fixTable5c(Map<String, SEFSubmission> sefSubmissions, SEFSubmission aggregatedSubmission) {
		Table5C aggregatedTable5b = aggregatedSubmission.getTable5C();
		final AnnualTransactionPPSR annualTransactionToFix = TableUtil.getAnnualTransactionPpsr(
				aggregatedTable5b.getAnnualTransactionsPPSR().getAnnualTransactionPPSR(), aggregatedSubmission.getHeader().getReportedYear());		
		final TotalAdditionSubtraction totalToFix = aggregatedTable5b.getTotalAdditionSubtraction();
		for (SEFSubmission reg : sefSubmissions.values()) {
			Table2C regTable = reg.getTable2C();
			for (PPSRTransfer exTr : regTable.getPPSRTransfer()) {
				String registryCode = exTr.getRegistry();
				if (belongsToAggregationGroup(registryCode)) {					
					// here fix table5a by reducing the values for the reported year and the total					
					fixAdditionSubtraction(annualTransactionToFix.getAdditions(), annualTransactionToFix.getSubtractions(), exTr);									
					fixAdditionSubtraction(totalToFix.getAdditions(), totalToFix.getSubtractions(), exTr);
				} else {
					// do nothing - it is already covered in aggregateTable2b
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
	 * Method that fixes table 5a
	 * @param additionsToFix
	 * @param subtractionsToFix
	 * @param exTr
	 */
	private void fixAdditionSubtraction(Additions additionsToFix, Subtractions subtractionsToFix, PPSRTransfer exTr) {
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
	private void aggregateTable5e(Map<String, SEFSubmission> sefSubmissions, SEFSubmission aggregated) {
		Table5E aggTable = aggregated.getTable5E();
		for (SEFSubmission reg : sefSubmissions.values()) {
			Table5E regTable = reg.getTable5E();
			for (AnnualRetirement regRetirement : regTable.getAnnualRetirements().getAnnualRetirement()) {
				AnnualRetirement aggRetirement = TableUtil.getSef2AnnualRetirement(aggTable.getAnnualRetirements()
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
	private void aggregateTable5d(Map<String, SEFSubmission> sefSubmissions, SEFSubmission aggregated) {
		Table5D aggTable = aggregated.getTable5D();
		for (SEFSubmission reg : sefSubmissions.values()) {
			Table5D regTable = reg.getTable5D();
			for (AnnualReplacement regReplacement : regTable.getAnnualReplacements().getAnnualReplacement()) {
				AnnualReplacement aggReplacement = TableUtil.getSef2AnnualReplacementByYear(aggTable
						.getAnnualReplacements().getAnnualReplacement(), regReplacement.getYear());
				aggregateCerList(aggReplacement.getRequirementForReplacement().getCerQty(), regReplacement
						.getRequirementForReplacement().getCerQty());
				aggregate(aggReplacement.getReplacement().getUnitQty(), regReplacement.getReplacement().getUnitQty());
				aggregate(aggReplacement.getCancellation().getUnitQty(), regReplacement.getCancellation().getUnitQty());
			}
			aggregateCerList(aggTable.getTotalTxOrEvent().getRequirementForReplacement().getCerQty(), regTable
					.getTotalTxOrEvent().getRequirementForReplacement().getCerQty());
			aggregate(aggTable.getTotalTxOrEvent().getReplacement().getUnitQty(), regTable.getTotalTxOrEvent()
					.getReplacement().getUnitQty());
			aggregate(aggTable.getTotalTxOrEvent().getCancellation().getUnitQty(), regTable.getTotalTxOrEvent()
					.getCancellation().getUnitQty());
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
			for (AnnualTransactionPPSR regPpsr : regTable.getAnnualTransactionsPPSR().getAnnualTransactionPPSR()) {
				AnnualTransactionPPSR aggPpsr = TableUtil.getAnnualTransactionPpsr(aggTable.getAnnualTransactionsPPSR()
						.getAnnualTransactionPPSR(), regPpsr.getYear());
				aggregate(aggPpsr.getAdditions(), regPpsr.getAdditions());
				aggregate(aggPpsr.getSubtractions(), regPpsr.getSubtractions());
			}
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
	private void aggregateTable5b(Map<String, SEFSubmission> sefSubmissions, SEFSubmission aggregated) {
		Table5B aggTable = aggregated.getTable5B();
		for (SEFSubmission reg : sefSubmissions.values()) {
			Table5B regTable = reg.getTable5B();
			for (AnnualTransaction regAnnual : regTable.getAnnualTransactions().getAnnualTransaction()) {
				AnnualTransaction aggAnnual = TableUtil.getSef2AnnualTransactionByYear(aggTable.getAnnualTransactions()
						.getAnnualTransaction(), regAnnual.getYear());
				aggregate(aggAnnual.getAdditions(), regAnnual.getAdditions());
				aggregate(aggAnnual.getSubtractions(), regAnnual.getSubtractions());
			}
			aggregate(aggTable.getTotalAdditionSubtraction().getAdditions(), regTable.getTotalAdditionSubtraction()
					.getAdditions());
			aggregate(aggTable.getTotalAdditionSubtraction().getSubtractions(), regTable.getTotalAdditionSubtraction()
					.getSubtractions());
		}
	}

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
				TransactionOrEventType aggType = TableUtil.getSef2TransactionOrEventTypeByType(
						aggTable.getTransactionOrEventType(), regType.getName());
				aggregateCerList(aggType.getRequirementForReplacement().getCerQty(), regType
						.getRequirementForReplacement().getCerQty());
				aggregate(aggType.getReplacement().getUnitQty(), regType.getReplacement().getUnitQty());
				aggregate(aggType.getCancellation().getUnitQty(), regType.getCancellation().getUnitQty());
			}
			aggregateCerList(aggTable.getTotalTxOrEvent().getRequirementForReplacement().getCerQty(), regTable
					.getTotalTxOrEvent().getRequirementForReplacement().getCerQty());
			aggregate(aggTable.getTotalTxOrEvent().getReplacement().getUnitQty(), regTable.getTotalTxOrEvent()
					.getReplacement().getUnitQty());
			aggregate(aggTable.getTotalTxOrEvent().getCancellation().getUnitQty(), regTable.getTotalTxOrEvent()
					.getCancellation().getUnitQty());
		}

	}

	/**
	 * @param aggregatedSubmission
	 */
	private void calculateTable2eAggregations(SEFSubmission aggregatedSubmission) {
		aggregate(aggregatedSubmission.getTable2E().getAdditions(), aggregatedSubmission.getTable2A().getSubTotal()
				.getAdditions());
		aggregate(aggregatedSubmission.getTable2E().getAdditions(), aggregatedSubmission.getTable2B().getSubTotal()
				.getAdditions());

		aggregate(aggregatedSubmission.getTable2E().getSubtractions(), aggregatedSubmission.getTable2A().getSubTotal()
				.getSubtractions());
		aggregate(aggregatedSubmission.getTable2E().getSubtractions(), aggregatedSubmission.getTable2B().getSubTotal()
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

		}
		return aggregated;
	}

	/**
	 * 
	 * @param sefSubmissions
	 * @param aggregated
	 * @return
	 */
	private SEFSubmission aggregateTable2d(Map<String, SEFSubmission> sefSubmissions, SEFSubmission aggregated) {
		Table2D aggTable = aggregated.getTable2D();
		for (SEFSubmission reg : sefSubmissions.values()) {
			Table2D regTable = reg.getTable2D();
			for (AdaptationFundType regType : regTable.getAdaptationFundType()) {
				AdaptationFundType aggType = TableUtil.getAdaptationFund(aggTable.getAdaptationFundType(),
						regType.getName());
				aggregate(aggType.getAmountTransferred().getUnitQty(), regType.getAmountTransferred().getUnitQty());
				aggregate(aggType.getAmountContributed().getUnitQty(), regType.getAmountContributed().getUnitQty());
			}

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

	private SEFSubmission aggregateTable2c(Map<String, SEFSubmission> sefSubmissions, SEFSubmission aggregated) {
		Table2C aggTable = aggregated.getTable2C();
		for (SEFSubmission reg : sefSubmissions.values()) {
			Table2C regTable = reg.getTable2C();
			for (PPSRTransfer ppsrTransfer : regTable.getPPSRTransfer()) {
				String registryCode = ppsrTransfer.getRegistry();
				if (!belongsToAggregationGroup(registryCode)) {
					PPSRTransfer aggPpsrTransfer = TableUtil.getPpsrTransfer(aggTable.getPPSRTransfer(), registryCode);
					if (aggPpsrTransfer == null) {
						aggPpsrTransfer = RF2TableFactory.createPpsrTransfer(registryCode);
						aggTable.getPPSRTransfer().add(aggPpsrTransfer);
					}
					// aggregate additions
					aggregate(aggPpsrTransfer.getAdditions(), ppsrTransfer.getAdditions());
					aggregate(aggTable.getSubTotal().getAdditions(), ppsrTransfer.getAdditions());
					// aggregate subtractions
					aggregate(aggPpsrTransfer.getSubtractions(), ppsrTransfer.getSubtractions());
					aggregate(aggTable.getSubTotal().getSubtractions(), ppsrTransfer.getSubtractions());
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
	 * @param sefSubmissions
	 * @param aggregated
	 * @return
	 */
	private SEFSubmission aggregateTable2aRetirement(Map<String, SEFSubmission> sefSubmissions, SEFSubmission aggregated) {
		Table2ARetirement aggTable = aggregated.getTable2ARetirement();
		for (SEFSubmission reg : sefSubmissions.values()) {
			Table2ARetirement regTable = reg.getTable2ARetirement();
			for (Retirement regRetirement : regTable.getRetirement()) {
				Retirement aggRetirement = TableUtil.getRetirementByName(aggTable.getRetirement(),
						regRetirement.getName());
				for (UnitQty qty : regRetirement.getUnitQty()) {
					UnitQty aggQTy = UnitQtyUtil.getUnitQtyByUnitType(aggRetirement.getUnitQty(), qty.getType());
					aggregate(aggQTy, qty);
				}
			}
			aggregate(aggTable.getTotalUnitQty(), regTable.getTotalUnitQty());
		}

		return aggregated;
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
