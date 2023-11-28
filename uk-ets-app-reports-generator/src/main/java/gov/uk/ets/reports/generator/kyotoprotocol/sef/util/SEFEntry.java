package gov.uk.ets.reports.generator.kyotoprotocol.sef.util;

import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.*;

import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.Date;

/**
 * @author gkountak
 *
 */
public class SEFEntry {

    private String transactionId;
    private Date transactionStatusDatetime;
    private ITLTransactionTypeEnum transactionTypeCode;
    private String transferringRegistryCode;
    private String acquiringRegistryCode;
    private ITLAccountTypeEnum transferringAccountType;
    private ITLAccountTypeEnum acquiringAccountType;
    private ITLNotificationTypeEnum notificationTypeCode;
    private ITLUnitTypeEnum unitTypeCode;
    private ITLLulucfTypeEnum blockLulucfCode;
    private ITLLulucfTypeEnum notifLulucfCode;
    private ITLCommitmentPeriodEnum applicablePeriodCode;
    private Long amount;
    private String track;
    //new fields for cp2
    private Date expiryDate;
    private String firstAauTransferFlag;
    private boolean isFirstAauTransferFlag;
    private ITLCommitmentPeriodEnum originalPeriodCode;

    public static final String CDM_REGISTRY_CODE = "CDM";

    /**
     * @return the transactionId
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * @param transactionId
     *            the transactionId to set
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * @return the transactionStatusDatetime
     */
    public Date getTransactionStatusDatetime() {
        return transactionStatusDatetime;
    }

    /**
     * @param transactionStatusDatetime
     *            the transactionStatusDatetime to set
     */
    public void setTransactionStatusDatetime(Date transactionStatusDatetime) {
        this.transactionStatusDatetime = transactionStatusDatetime;
    }

    /**
     * Helper to get the transaction year.
     *
     * @return
     */
    public int getTransactionYear() {
        if (getTransactionStatusDatetime() == null) {
            throw new InvalidParameterException("Cannot get the YEAR of a null date.");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(getTransactionStatusDatetime());
        return cal.get(Calendar.YEAR);
    }

    /**
     * @return the transactionTypeCode
     */
    public ITLTransactionTypeEnum getTransactionTypeCode() {
        return transactionTypeCode;
    }

    /**
     * @param transactionTypeCode
     *            the transactionTypeCode to set
     */
    public void setTransactionTypeCode(ITLTransactionTypeEnum transactionTypeCode) {
        this.transactionTypeCode = transactionTypeCode;
    }

    /**
     * @return the transferringRegistryCode
     */
    public String getTransferringRegistryCode() {
        return transferringRegistryCode;
    }

    /**
     * @param transferringRegistryCode
     *            the transferringRegistryCode to set
     */
    public void setTransferringRegistryCode(String transferringRegistryCode) {
        this.transferringRegistryCode = transferringRegistryCode;
    }

    /**
     * @return the acquiringRegistryCode
     */
    public String sefSubmission() {
        return acquiringRegistryCode;
    }

    /**
     * @param acquiringRegistryCode
     *            the acquiringRegistryCode to set
     */
    public void setAcquiringRegistryCode(String acquiringRegistryCode) {
        this.acquiringRegistryCode = acquiringRegistryCode;
    }

    /**
     *
     * @return
     */
    public String getAcquiringRegistryCode() {
        return this.acquiringRegistryCode;
    }

    /**
     * @return the transferringAccountType
     */
    public ITLAccountTypeEnum getTransferringAccountType() {
        return transferringAccountType;
    }

    /**
     * @param transferringAccountType
     *            the transferringAccountType to set
     */
    public void setTransferringAccountType(ITLAccountTypeEnum transferringAccountType) {
        this.transferringAccountType = transferringAccountType;
    }

    /**
     * @return the acquiringAccountType
     */
    public ITLAccountTypeEnum getAcquiringAccountType() {
        return acquiringAccountType;
    }

    /**
     * @param acquiringAccountType
     *            the acquiringAccountType to set
     */
    public void setAcquiringAccountType(ITLAccountTypeEnum acquiringAccountType) {
        this.acquiringAccountType = acquiringAccountType;
    }

    /**
     * @return the notificationTypeCode
     */
    public ITLNotificationTypeEnum getNotificationTypeCode() {
        return notificationTypeCode;
    }

    /**
     * @param notificationTypeCode
     *            the notificationTypeCode to set
     */
    public void setNotificationTypeCode(ITLNotificationTypeEnum notificationTypeCode) {
        this.notificationTypeCode = notificationTypeCode;
    }

    /**
     * @return the unitTypeCode
     */
    public ITLUnitTypeEnum getUnitTypeCode() {
        return unitTypeCode;
    }

    /**
     * @param unitTypeCode
     *            the unitTypeCode to set
     */
    public void setUnitTypeCode(ITLUnitTypeEnum unitTypeCode) {
        this.unitTypeCode = unitTypeCode;
    }

    /**
     * @return the blockLulucfCode
     */
    public ITLLulucfTypeEnum getBlockLulucfCode() {
        return blockLulucfCode;
    }

    /**
     * @param blockLulucfCode
     *            the blockLulucfCode to set
     */
    public void setBlockLulucfCode(ITLLulucfTypeEnum blockLulucfCode) {
        this.blockLulucfCode = blockLulucfCode;
    }

    /**
     * @return the notifLulucfCode
     */
    public ITLLulucfTypeEnum getNotifLulucfCode() {
        return notifLulucfCode;
    }

    /**
     * @param notifLulucfCode
     *            the notifLulucfCode to set
     */
    public void setNotifLulucfCode(ITLLulucfTypeEnum notifLulucfCode) {
        this.notifLulucfCode = notifLulucfCode;
    }

    /**
     * @return the applicablePeriodCode
     */
    public ITLCommitmentPeriodEnum getApplicablePeriodCode() {
        return applicablePeriodCode;
    }

    /**
     * @param applicablePeriodCode
     *            the applicablePeriodCode to set
     */
    public void setApplicablePeriodCode(ITLCommitmentPeriodEnum applicablePeriodCode) {
        this.applicablePeriodCode = applicablePeriodCode;
    }

    /**
     *
     * @return
     */
    public ITLCommitmentPeriodEnum getOriginalPeriodCode() {
        return originalPeriodCode;
    }

    /**
     *
     * @param originalPeriodCode
     */
    public void setOriginalPeriodCode(ITLCommitmentPeriodEnum originalPeriodCode) {
        this.originalPeriodCode = originalPeriodCode;
    }

    /**
     * @return the amount
     */
    public Long getAmount() {
        return amount;
    }

    /**
     * @param amount
     *            the amount to set
     */
    public void setAmount(Long amount) {
        this.amount = amount;
    }

    /**
     * @return the track
     */
    public String getTrack() {
        return track;
    }

    /**
     * @param track
     *            the track to set
     */
    public void setTrack(String track) {
        this.track = track;
    }

    /**
     * @return the expiryDate
     */
    public Date getExpiryDate() {
        return expiryDate;
    }

    /**
     * @param expiryDate the expiryDate to set
     */
    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    /**
     * @return the firstAauTransferFlag
     */
    public String getFirstAauTransferFlag() {
        return firstAauTransferFlag;
    }

    /**
     * @param firstAauTransferFlag the firstAauTransferFlag to set
     */
    public void setFirstAauTransferFlag(String firstAauTransferFlag) {

        this.firstAauTransferFlag = firstAauTransferFlag;

        if ("T".equals(firstAauTransferFlag)) {
            setFirstAauTransferFlag(true);
        } else {
            setFirstAauTransferFlag(false);
        }
    }

    /**
     * @return
     */
    public boolean isCDMTheAcquiringRegistry() {
        return SEFEntry.CDM_REGISTRY_CODE.equals(getAcquiringRegistryCode());
    }

    /**
     * @return
     */
    public boolean isCDMTheTransferringRegistry() {
        return SEFEntry.CDM_REGISTRY_CODE.equals(getTransferringRegistryCode());
    }

    /**
     *
     * @return
     */
    public boolean isFirstAauTransferFlag() {
        return isFirstAauTransferFlag;
    }

    /**
     *  if firstAauTransferFlag is T or F or null
     * @param isFirstAauTransferFlag
     */
    public void setFirstAauTransferFlag(boolean isFirstAauTransferFlag) {
        this.isFirstAauTransferFlag = isFirstAauTransferFlag;
    }


}

