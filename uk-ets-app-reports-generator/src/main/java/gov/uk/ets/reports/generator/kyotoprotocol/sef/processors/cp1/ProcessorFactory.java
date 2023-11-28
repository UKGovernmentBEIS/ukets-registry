package gov.uk.ets.reports.generator.kyotoprotocol.sef.processors.cp1;


import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ITLTransactionTypeEnum;

/**
 * @author gkountak
 * 
 */
public class ProcessorFactory {

	public static AbstractTransactionProcessor createForFirstTrack2Trabnsfer() {
		return new FirstTrack2TransferProcessor();
	}
	
	public static AbstractTransactionProcessor createForNotificationProcess() {
		return new NotificationProcessor();
	}
	
	/**
	 * @return
	 */
	public static AggregatorProcessor createForAggregation() {
		return new AggregatorProcessor();
	}
	
	public static AbstractTransactionProcessor create(ITLTransactionTypeEnum txType) {
		if (ITLTransactionTypeEnum.EXTERNAL_TRANSFER == txType) {
			throw new IllegalArgumentException("For external transfers please use the create(ITLTransactionTypeEnum txType, Boolean isTR) method");
		}
		return create(txType, null);
	}
	
	public static AbstractTransactionProcessor create(ITLTransactionTypeEnum txType, Boolean isTR) {
		AbstractTransactionProcessor processor = null;
		switch (txType) {
		case CANCELLATION:
			processor = new CancellationProcessor();
			break;
		case CARRY_OVER:
			processor = new CarryOverProcessor();
			break;
		case CONVERSION:
			processor = new ConversionProcessor();
			break;
		case EXTERNAL_TRANSFER:
			processor = isTR ? new ExternalTransferProcessorTR() : new ExternalTransferProcessorAR();
			break;
		case INTERNAL_TRANSFER:
			processor = new InternalTransferProcessor();
			break;
		case ISSUANCE:
			processor = new IssuanceProcessor();
			break;
		case REPLACEMENT:
			processor = new ReplacementProcessor();
			break;
		case RETIREMENT:
			processor = new RetirementProcessor();
			break;
		default:
			break;
		}
		return processor;
	}

	
}
