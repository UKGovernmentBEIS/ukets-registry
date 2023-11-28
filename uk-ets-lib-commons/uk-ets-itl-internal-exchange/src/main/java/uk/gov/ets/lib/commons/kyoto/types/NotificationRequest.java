/**
 * NotificationRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.gov.ets.lib.commons.kyoto.types;

public class NotificationRequest  implements java.io.Serializable {
    private java.lang.String from;

    private java.lang.String to;

    private int majorVersion;

    private int minorVersion;

    private java.lang.String transactionIdentifier;

    private int transactionStatus;

    private int partyType;

    private uk.gov.ets.lib.commons.kyoto.types.EvaluationResult[] evaluationResult;

    public NotificationRequest() {
    }

    public NotificationRequest(
           java.lang.String from,
           java.lang.String to,
           int majorVersion,
           int minorVersion,
           java.lang.String transactionIdentifier,
           int transactionStatus,
           int partyType,
           uk.gov.ets.lib.commons.kyoto.types.EvaluationResult[] evaluationResult) {
           this.from = from;
           this.to = to;
           this.majorVersion = majorVersion;
           this.minorVersion = minorVersion;
           this.transactionIdentifier = transactionIdentifier;
           this.transactionStatus = transactionStatus;
           this.partyType = partyType;
           this.evaluationResult = evaluationResult;
    }


    /**
     * Gets the from value for this NotificationRequest.
     * 
     * @return from
     */
    public java.lang.String getFrom() {
        return from;
    }


    /**
     * Sets the from value for this NotificationRequest.
     * 
     * @param from
     */
    public void setFrom(java.lang.String from) {
        this.from = from;
    }


    /**
     * Gets the to value for this NotificationRequest.
     * 
     * @return to
     */
    public java.lang.String getTo() {
        return to;
    }


    /**
     * Sets the to value for this NotificationRequest.
     * 
     * @param to
     */
    public void setTo(java.lang.String to) {
        this.to = to;
    }


    /**
     * Gets the majorVersion value for this NotificationRequest.
     * 
     * @return majorVersion
     */
    public int getMajorVersion() {
        return majorVersion;
    }


    /**
     * Sets the majorVersion value for this NotificationRequest.
     * 
     * @param majorVersion
     */
    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }


    /**
     * Gets the minorVersion value for this NotificationRequest.
     * 
     * @return minorVersion
     */
    public int getMinorVersion() {
        return minorVersion;
    }


    /**
     * Sets the minorVersion value for this NotificationRequest.
     * 
     * @param minorVersion
     */
    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }


    /**
     * Gets the transactionIdentifier value for this NotificationRequest.
     * 
     * @return transactionIdentifier
     */
    public java.lang.String getTransactionIdentifier() {
        return transactionIdentifier;
    }


    /**
     * Sets the transactionIdentifier value for this NotificationRequest.
     * 
     * @param transactionIdentifier
     */
    public void setTransactionIdentifier(java.lang.String transactionIdentifier) {
        this.transactionIdentifier = transactionIdentifier;
    }


    /**
     * Gets the transactionStatus value for this NotificationRequest.
     * 
     * @return transactionStatus
     */
    public int getTransactionStatus() {
        return transactionStatus;
    }


    /**
     * Sets the transactionStatus value for this NotificationRequest.
     * 
     * @param transactionStatus
     */
    public void setTransactionStatus(int transactionStatus) {
        this.transactionStatus = transactionStatus;
    }


    /**
     * Gets the partyType value for this NotificationRequest.
     * 
     * @return partyType
     */
    public int getPartyType() {
        return partyType;
    }


    /**
     * Sets the partyType value for this NotificationRequest.
     * 
     * @param partyType
     */
    public void setPartyType(int partyType) {
        this.partyType = partyType;
    }


    /**
     * Gets the evaluationResult value for this NotificationRequest.
     * 
     * @return evaluationResult
     */
    public uk.gov.ets.lib.commons.kyoto.types.EvaluationResult[] getEvaluationResult() {
        return evaluationResult;
    }


    /**
     * Sets the evaluationResult value for this NotificationRequest.
     * 
     * @param evaluationResult
     */
    public void setEvaluationResult(uk.gov.ets.lib.commons.kyoto.types.EvaluationResult[] evaluationResult) {
        this.evaluationResult = evaluationResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof NotificationRequest)) return false;
        NotificationRequest other = (NotificationRequest) obj;
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
            ((this.transactionIdentifier==null && other.getTransactionIdentifier()==null) || 
             (this.transactionIdentifier!=null &&
              this.transactionIdentifier.equals(other.getTransactionIdentifier()))) &&
            this.transactionStatus == other.getTransactionStatus() &&
            this.partyType == other.getPartyType() &&
            ((this.evaluationResult==null && other.getEvaluationResult()==null) || 
             (this.evaluationResult!=null &&
              java.util.Arrays.equals(this.evaluationResult, other.getEvaluationResult())));
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
        if (getTransactionIdentifier() != null) {
            _hashCode += getTransactionIdentifier().hashCode();
        }
        _hashCode += getTransactionStatus();
        _hashCode += getPartyType();
        if (getEvaluationResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getEvaluationResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getEvaluationResult(), i);
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
