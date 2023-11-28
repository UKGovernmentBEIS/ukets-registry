/**
 * ITLNoticeRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.gov.ets.lib.commons.kyoto.types;

public class ITLNoticeRequest  implements java.io.Serializable {
    private java.lang.String from;

    private java.lang.String to;

    private int majorVersion;

    private int minorVersion;

    private java.lang.String messageContent;

    private java.util.Calendar messageDate;

    private int notificationType;

    private long notificationIdentifier;

    private int notificationStatus;

    private java.lang.String projectNumber;

    private java.lang.Integer unitType;

    private java.lang.Long targetValue;

    private java.util.Date targetDate;

    private java.lang.Long LULUCFActivity;

    private java.lang.Integer commitPeriod;

    private java.util.Date actionDueDate;

    private uk.gov.ets.lib.commons.kyoto.types.UnitBlockIdentifier[] unitBlockIdentifiers;

    public ITLNoticeRequest() {
    }

    public ITLNoticeRequest(
           java.lang.String from,
           java.lang.String to,
           int majorVersion,
           int minorVersion,
           java.lang.String messageContent,
           java.util.Calendar messageDate,
           int notificationType,
           long notificationIdentifier,
           int notificationStatus,
           java.lang.String projectNumber,
           java.lang.Integer unitType,
           java.lang.Long targetValue,
           java.util.Date targetDate,
           java.lang.Long LULUCFActivity,
           java.lang.Integer commitPeriod,
           java.util.Date actionDueDate,
           uk.gov.ets.lib.commons.kyoto.types.UnitBlockIdentifier[] unitBlockIdentifiers) {
           this.from = from;
           this.to = to;
           this.majorVersion = majorVersion;
           this.minorVersion = minorVersion;
           this.messageContent = messageContent;
           this.messageDate = messageDate;
           this.notificationType = notificationType;
           this.notificationIdentifier = notificationIdentifier;
           this.notificationStatus = notificationStatus;
           this.projectNumber = projectNumber;
           this.unitType = unitType;
           this.targetValue = targetValue;
           this.targetDate = targetDate;
           this.LULUCFActivity = LULUCFActivity;
           this.commitPeriod = commitPeriod;
           this.actionDueDate = actionDueDate;
           this.unitBlockIdentifiers = unitBlockIdentifiers;
    }


    /**
     * Gets the from value for this ITLNoticeRequest.
     * 
     * @return from
     */
    public java.lang.String getFrom() {
        return from;
    }


    /**
     * Sets the from value for this ITLNoticeRequest.
     * 
     * @param from
     */
    public void setFrom(java.lang.String from) {
        this.from = from;
    }


    /**
     * Gets the to value for this ITLNoticeRequest.
     * 
     * @return to
     */
    public java.lang.String getTo() {
        return to;
    }


    /**
     * Sets the to value for this ITLNoticeRequest.
     * 
     * @param to
     */
    public void setTo(java.lang.String to) {
        this.to = to;
    }


    /**
     * Gets the majorVersion value for this ITLNoticeRequest.
     * 
     * @return majorVersion
     */
    public int getMajorVersion() {
        return majorVersion;
    }


    /**
     * Sets the majorVersion value for this ITLNoticeRequest.
     * 
     * @param majorVersion
     */
    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }


    /**
     * Gets the minorVersion value for this ITLNoticeRequest.
     * 
     * @return minorVersion
     */
    public int getMinorVersion() {
        return minorVersion;
    }


    /**
     * Sets the minorVersion value for this ITLNoticeRequest.
     * 
     * @param minorVersion
     */
    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }


    /**
     * Gets the messageContent value for this ITLNoticeRequest.
     * 
     * @return messageContent
     */
    public java.lang.String getMessageContent() {
        return messageContent;
    }


    /**
     * Sets the messageContent value for this ITLNoticeRequest.
     * 
     * @param messageContent
     */
    public void setMessageContent(java.lang.String messageContent) {
        this.messageContent = messageContent;
    }


    /**
     * Gets the messageDate value for this ITLNoticeRequest.
     * 
     * @return messageDate
     */
    public java.util.Calendar getMessageDate() {
        return messageDate;
    }


    /**
     * Sets the messageDate value for this ITLNoticeRequest.
     * 
     * @param messageDate
     */
    public void setMessageDate(java.util.Calendar messageDate) {
        this.messageDate = messageDate;
    }


    /**
     * Gets the notificationType value for this ITLNoticeRequest.
     * 
     * @return notificationType
     */
    public int getNotificationType() {
        return notificationType;
    }


    /**
     * Sets the notificationType value for this ITLNoticeRequest.
     * 
     * @param notificationType
     */
    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }


    /**
     * Gets the notificationIdentifier value for this ITLNoticeRequest.
     * 
     * @return notificationIdentifier
     */
    public long getNotificationIdentifier() {
        return notificationIdentifier;
    }


    /**
     * Sets the notificationIdentifier value for this ITLNoticeRequest.
     * 
     * @param notificationIdentifier
     */
    public void setNotificationIdentifier(long notificationIdentifier) {
        this.notificationIdentifier = notificationIdentifier;
    }


    /**
     * Gets the notificationStatus value for this ITLNoticeRequest.
     * 
     * @return notificationStatus
     */
    public int getNotificationStatus() {
        return notificationStatus;
    }


    /**
     * Sets the notificationStatus value for this ITLNoticeRequest.
     * 
     * @param notificationStatus
     */
    public void setNotificationStatus(int notificationStatus) {
        this.notificationStatus = notificationStatus;
    }


    /**
     * Gets the projectNumber value for this ITLNoticeRequest.
     * 
     * @return projectNumber
     */
    public java.lang.String getProjectNumber() {
        return projectNumber;
    }


    /**
     * Sets the projectNumber value for this ITLNoticeRequest.
     * 
     * @param projectNumber
     */
    public void setProjectNumber(java.lang.String projectNumber) {
        this.projectNumber = projectNumber;
    }


    /**
     * Gets the unitType value for this ITLNoticeRequest.
     * 
     * @return unitType
     */
    public java.lang.Integer getUnitType() {
        return unitType;
    }


    /**
     * Sets the unitType value for this ITLNoticeRequest.
     * 
     * @param unitType
     */
    public void setUnitType(java.lang.Integer unitType) {
        this.unitType = unitType;
    }


    /**
     * Gets the targetValue value for this ITLNoticeRequest.
     * 
     * @return targetValue
     */
    public java.lang.Long getTargetValue() {
        return targetValue;
    }


    /**
     * Sets the targetValue value for this ITLNoticeRequest.
     * 
     * @param targetValue
     */
    public void setTargetValue(java.lang.Long targetValue) {
        this.targetValue = targetValue;
    }


    /**
     * Gets the targetDate value for this ITLNoticeRequest.
     * 
     * @return targetDate
     */
    public java.util.Date getTargetDate() {
        return targetDate;
    }


    /**
     * Sets the targetDate value for this ITLNoticeRequest.
     * 
     * @param targetDate
     */
    public void setTargetDate(java.util.Date targetDate) {
        this.targetDate = targetDate;
    }


    /**
     * Gets the LULUCFActivity value for this ITLNoticeRequest.
     * 
     * @return LULUCFActivity
     */
    public java.lang.Long getLULUCFActivity() {
        return LULUCFActivity;
    }


    /**
     * Sets the LULUCFActivity value for this ITLNoticeRequest.
     * 
     * @param LULUCFActivity
     */
    public void setLULUCFActivity(java.lang.Long LULUCFActivity) {
        this.LULUCFActivity = LULUCFActivity;
    }


    /**
     * Gets the commitPeriod value for this ITLNoticeRequest.
     * 
     * @return commitPeriod
     */
    public java.lang.Integer getCommitPeriod() {
        return commitPeriod;
    }


    /**
     * Sets the commitPeriod value for this ITLNoticeRequest.
     * 
     * @param commitPeriod
     */
    public void setCommitPeriod(java.lang.Integer commitPeriod) {
        this.commitPeriod = commitPeriod;
    }


    /**
     * Gets the actionDueDate value for this ITLNoticeRequest.
     * 
     * @return actionDueDate
     */
    public java.util.Date getActionDueDate() {
        return actionDueDate;
    }


    /**
     * Sets the actionDueDate value for this ITLNoticeRequest.
     * 
     * @param actionDueDate
     */
    public void setActionDueDate(java.util.Date actionDueDate) {
        this.actionDueDate = actionDueDate;
    }


    /**
     * Gets the unitBlockIdentifiers value for this ITLNoticeRequest.
     * 
     * @return unitBlockIdentifiers
     */
    public uk.gov.ets.lib.commons.kyoto.types.UnitBlockIdentifier[] getUnitBlockIdentifiers() {
        return unitBlockIdentifiers;
    }


    /**
     * Sets the unitBlockIdentifiers value for this ITLNoticeRequest.
     * 
     * @param unitBlockIdentifiers
     */
    public void setUnitBlockIdentifiers(uk.gov.ets.lib.commons.kyoto.types.UnitBlockIdentifier[] unitBlockIdentifiers) {
        this.unitBlockIdentifiers = unitBlockIdentifiers;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ITLNoticeRequest)) return false;
        ITLNoticeRequest other = (ITLNoticeRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.from==null && other.getFrom()==null) || 
             (this.from!=null &&
              this.from.equals(other.getFrom()))) &&
            ((this.to==null && other.getTo()==null) || 
             (this.to!=null &&
              this.to.equals(other.getTo()))) &&
            this.majorVersion == other.getMajorVersion() &&
            this.minorVersion == other.getMinorVersion() &&
            ((this.messageContent==null && other.getMessageContent()==null) || 
             (this.messageContent!=null &&
              this.messageContent.equals(other.getMessageContent()))) &&
            ((this.messageDate==null && other.getMessageDate()==null) || 
             (this.messageDate!=null &&
              this.messageDate.equals(other.getMessageDate()))) &&
            this.notificationType == other.getNotificationType() &&
            this.notificationIdentifier == other.getNotificationIdentifier() &&
            this.notificationStatus == other.getNotificationStatus() &&
            ((this.projectNumber==null && other.getProjectNumber()==null) || 
             (this.projectNumber!=null &&
              this.projectNumber.equals(other.getProjectNumber()))) &&
            ((this.unitType==null && other.getUnitType()==null) || 
             (this.unitType!=null &&
              this.unitType.equals(other.getUnitType()))) &&
            ((this.targetValue==null && other.getTargetValue()==null) || 
             (this.targetValue!=null &&
              this.targetValue.equals(other.getTargetValue()))) &&
            ((this.targetDate==null && other.getTargetDate()==null) || 
             (this.targetDate!=null &&
              this.targetDate.equals(other.getTargetDate()))) &&
            ((this.LULUCFActivity==null && other.getLULUCFActivity()==null) || 
             (this.LULUCFActivity!=null &&
              this.LULUCFActivity.equals(other.getLULUCFActivity()))) &&
            ((this.commitPeriod==null && other.getCommitPeriod()==null) || 
             (this.commitPeriod!=null &&
              this.commitPeriod.equals(other.getCommitPeriod()))) &&
            ((this.actionDueDate==null && other.getActionDueDate()==null) || 
             (this.actionDueDate!=null &&
              this.actionDueDate.equals(other.getActionDueDate()))) &&
            ((this.unitBlockIdentifiers==null && other.getUnitBlockIdentifiers()==null) || 
             (this.unitBlockIdentifiers!=null &&
              java.util.Arrays.equals(this.unitBlockIdentifiers, other.getUnitBlockIdentifiers())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getFrom() != null) {
            _hashCode += getFrom().hashCode();
        }
        if (getTo() != null) {
            _hashCode += getTo().hashCode();
        }
        _hashCode += getMajorVersion();
        _hashCode += getMinorVersion();
        if (getMessageContent() != null) {
            _hashCode += getMessageContent().hashCode();
        }
        if (getMessageDate() != null) {
            _hashCode += getMessageDate().hashCode();
        }
        _hashCode += getNotificationType();
        _hashCode += new Long(getNotificationIdentifier()).hashCode();
        _hashCode += getNotificationStatus();
        if (getProjectNumber() != null) {
            _hashCode += getProjectNumber().hashCode();
        }
        if (getUnitType() != null) {
            _hashCode += getUnitType().hashCode();
        }
        if (getTargetValue() != null) {
            _hashCode += getTargetValue().hashCode();
        }
        if (getTargetDate() != null) {
            _hashCode += getTargetDate().hashCode();
        }
        if (getLULUCFActivity() != null) {
            _hashCode += getLULUCFActivity().hashCode();
        }
        if (getCommitPeriod() != null) {
            _hashCode += getCommitPeriod().hashCode();
        }
        if (getActionDueDate() != null) {
            _hashCode += getActionDueDate().hashCode();
        }
        if (getUnitBlockIdentifiers() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getUnitBlockIdentifiers());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getUnitBlockIdentifiers(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }
}
