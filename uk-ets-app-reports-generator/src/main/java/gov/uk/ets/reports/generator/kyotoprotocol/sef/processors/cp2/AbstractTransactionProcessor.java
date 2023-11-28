package gov.uk.ets.reports.generator.kyotoprotocol.sef.processors.cp2;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import gov.uk.ets.reports.generator.kyotoprotocol.commons.RF2TableFactory;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.enums.QuantityValueEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.enums.ReportFormatEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.util.HeaderUtils;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.CerQty;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SourceType;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.StatusType;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.UnitQty;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Header;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ITLCommitmentPeriodEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.CalendarUtils;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.NotificationEntry;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.SEFEntry;

/**
 * @author gkountak
 * 
 */
public abstract class AbstractTransactionProcessor extends AbstractUpdateProcessor {

	private int reportYear; // submissionYear

	private int submitYear; // reportedYear

	private int submitCP; // commitmentPeriod

    private Date reportEndDate; // reportEndDate

	private String comment;

	private static final short HEADER_VERSION = 1;
	
	public Map<String, SEFSubmission> process(Map<String, SEFSubmission> sefSubmissions, SEFEntry entry,
											  ITLCommitmentPeriodEnum reportCP, short reportedYear) {
		return sefSubmissions;
	}

	public Map<String, SEFSubmission> process(Map<String, SEFSubmission> sefSubmissions, NotificationEntry entry,
			ITLCommitmentPeriodEnum reportCP, short reportedYear) {
		return sefSubmissions;
	}

	public SEFSubmission getOrCreateSubmission(Map<String, SEFSubmission> sefSubmissions, String registryCode) {
		SEFSubmission sefSubmission = sefSubmissions.get(registryCode);
		if (sefSubmission == null) {

			sefSubmission = createSubmission(registryCode);
			sefSubmissions.put(registryCode, sefSubmission);
		}
		return sefSubmission;
	}

	/**
	 * Creates a {@link SEFSubmission} object
	 * @param registryCode
	 * @return
	 */
	public SEFSubmission createSubmission(String registryCode) {		
		SEFSubmission sefSubmission = new SEFSubmission();
		Header header = createHeader(registryCode);

		sefSubmission.setHeader(header);
		sefSubmission.setTable1(RF2TableFactory.initializeTable1());
		sefSubmission.setTable2A(RF2TableFactory.initializeTable2A());
		sefSubmission.setTable2ARetirement(RF2TableFactory.initializeTable2ARetirement());
		sefSubmission.setTable2B(RF2TableFactory.initializeTable2B());
		sefSubmission.setTable2C(RF2TableFactory.initializeTable2C());
		sefSubmission.setTable2D(RF2TableFactory.initializeTable2D());
		sefSubmission.setTable2E(RF2TableFactory.initializeTable2E());
		sefSubmission.setTable3(RF2TableFactory.initializeTable3());
		sefSubmission.setTable4(RF2TableFactory.initializeTable4());
		sefSubmission.setTable5A(RF2TableFactory.initializeTable5A());
		sefSubmission.setTable5B(RF2TableFactory.initializeTable5B(submitCP));
		sefSubmission.setTable5C(RF2TableFactory.initializeTable5C(submitCP));
		sefSubmission.setTable5D(RF2TableFactory.initializeTable5D(submitCP));
		sefSubmission.setTable5E(RF2TableFactory.initializeTable5E(submitCP));
		sefSubmission.setTable6A(RF2TableFactory.createTable6A());
		sefSubmission.setTable6B(RF2TableFactory.createTable6B());
		sefSubmission.setTable6C(RF2TableFactory.createTable6C());
		
		return sefSubmission;
	}

	/**
	 * Creates a {@link Header} object.
	 * @param registryCode the registry code
	 * @return
	 */
	private Header createHeader(String registryCode) {
		Header header = new Header();
		header.setCommitmentPeriod((byte) submitCP);		
		header.setParty(registryCode);
		header.setReportedYear((short) reportYear);
		header.setSubmissionYear((short) submitYear);
		header.setTimestamp(CalendarUtils.newXmlGregorianCalendar());
		header.setVersion(HEADER_VERSION);
		header.setSource(SourceType.REG);
		header.setStatus(StatusType.DRAFT);
		header.setReportFormat(ReportFormatEnum.CP2.value());
        if (reportEndDate != null) {
            header.setReportEndDate(HeaderUtils.formatSdfReportEndDateIn(reportEndDate));
        }
		return header;
	}

    /**
	 * Utility method. Returns the year field of the specified date
	 * 
	 * @param date
	 * @return the year field of the specified date
	 */
	int getYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		return cal.get(Calendar.YEAR);
	}

	public void setReportYear(int reportYear) {
		this.reportYear = reportYear;
	}

	public void setSubmitYear(int submitYear) {
		this.submitYear = submitYear;
	}

	public void setSubmitCP(int submitCP) {
		this.submitCP = submitCP;
	}

	public int getReportYear() {
		return reportYear;
	}

	public int getSubmitYear() {
		return submitYear;
	}

	public int getSubmitCP() {
		return submitCP;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 *            the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	/**
	 * Returns the quantity as a long value
	 * 
	 * @param qty
	 * @return the quantity as a long value
	 */
	protected long getLongValue(UnitQty qty) {
		if (isNoOrNa(qty.getValue())) {
			return 0L;
		}
		return Long.valueOf(qty.getValue());
	}

	/**
	 * Returns the quantity as a long value
	 * 
	 * @param qty
	 * @return the quantity as a long value
	 */
	protected long getLongValue(CerQty qty) {
		if (isNoOrNa(qty.getValue())) {
			return 0L;
		}
		return Long.valueOf(qty.getValue());
	}

	/**
	 * Returns true if the specified qtyString is equal to "NO" or "NA", false otherwise
	 * 
	 * @param qtyString
	 * @return true if the specified qtyString is equal to "NO" or "NA", false otherwise
	 */
	protected boolean isNoOrNa(String qtyString) {
		return QuantityValueEnum.NO.name().equals(qtyString) || (QuantityValueEnum.NA.name().equals(qtyString));
	}

    public void setReportEndDate(Date reportEndDate) {
        this.reportEndDate = reportEndDate;
    }
}