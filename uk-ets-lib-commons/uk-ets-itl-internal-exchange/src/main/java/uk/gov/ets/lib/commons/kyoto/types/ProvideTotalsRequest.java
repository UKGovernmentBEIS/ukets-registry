/**
 * ProvideTotalsRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.gov.ets.lib.commons.kyoto.types;

public class ProvideTotalsRequest  implements java.io.Serializable {
    private java.lang.String from;

    private java.lang.String to;

    private int majorVersion;

    private int minorVersion;

    private java.lang.String reconciliationIdentifier;

    private java.util.Calendar reconciliationSnapshotDatetime;

    private int reconciliationStatus;

    private java.lang.Integer unitType;

    private java.lang.Integer suppUnitType;

    private java.lang.Integer accountCommitPeriod;

    private java.lang.Integer accountType;

    private java.lang.Integer byAccountFlag;

    private int[] responseCodes;

    public ProvideTotalsRequest() {
    }

    public ProvideTotalsRequest(
           java.lang.String from,
           java.lang.String to,
           int majorVersion,
           int minorVersion,
           java.lang.String reconciliationIdentifier,
           java.util.Calendar reconciliationSnapshotDatetime,
           int reconciliationStatus,
           java.lang.Integer unitType,
           java.lang.Integer suppUnitType,
           java.lang.Integer accountCommitPeriod,
           java.lang.Integer accountType,
           java.lang.Integer byAccountFlag,
           int[] responseCodes) {
           this.from = from;
           this.to = to;
           this.majorVersion = majorVersion;
           this.minorVersion = minorVersion;
           this.reconciliationIdentifier = reconciliationIdentifier;
           this.reconciliationSnapshotDatetime = reconciliationSnapshotDatetime;
           this.reconciliationStatus = reconciliationStatus;
           this.unitType = unitType;
           this.suppUnitType = suppUnitType;
           this.accountCommitPeriod = accountCommitPeriod;
           this.accountType = accountType;
           this.byAccountFlag = byAccountFlag;
           this.responseCodes = responseCodes;
    }


    /**
     * Gets the from value for this ProvideTotalsRequest.
     * 
     * @return from
     */
    public java.lang.String getFrom() {
        return from;
    }


    /**
     * Sets the from value for this ProvideTotalsRequest.
     * 
     * @param from
     */
    public void setFrom(java.lang.String from) {
        this.from = from;
    }


    /**
     * Gets the to value for this ProvideTotalsRequest.
     * 
     * @return to
     */
    public java.lang.String getTo() {
        return to;
    }


    /**
     * Sets the to value for this ProvideTotalsRequest.
     * 
     * @param to
     */
    public void setTo(java.lang.String to) {
        this.to = to;
    }


    /**
     * Gets the majorVersion value for this ProvideTotalsRequest.
     * 
     * @return majorVersion
     */
    public int getMajorVersion() {
        return majorVersion;
    }


    /**
     * Sets the majorVersion value for this ProvideTotalsRequest.
     * 
     * @param majorVersion
     */
    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }


    /**
     * Gets the minorVersion value for this ProvideTotalsRequest.
     * 
     * @return minorVersion
     */
    public int getMinorVersion() {
        return minorVersion;
    }


    /**
     * Sets the minorVersion value for this ProvideTotalsRequest.
     * 
     * @param minorVersion
     */
    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }


    /**
     * Gets the reconciliationIdentifier value for this ProvideTotalsRequest.
     * 
     * @return reconciliationIdentifier
     */
    public java.lang.String getReconciliationIdentifier() {
        return reconciliationIdentifier;
    }


    /**
     * Sets the reconciliationIdentifier value for this ProvideTotalsRequest.
     * 
     * @param reconciliationIdentifier
     */
    public void setReconciliationIdentifier(java.lang.String reconciliationIdentifier) {
        this.reconciliationIdentifier = reconciliationIdentifier;
    }


    /**
     * Gets the reconciliationSnapshotDatetime value for this ProvideTotalsRequest.
     * 
     * @return reconciliationSnapshotDatetime
     */
    public java.util.Calendar getReconciliationSnapshotDatetime() {
        return reconciliationSnapshotDatetime;
    }


    /**
     * Sets the reconciliationSnapshotDatetime value for this ProvideTotalsRequest.
     * 
     * @param reconciliationSnapshotDatetime
     */
    public void setReconciliationSnapshotDatetime(java.util.Calendar reconciliationSnapshotDatetime) {
        this.reconciliationSnapshotDatetime = reconciliationSnapshotDatetime;
    }


    /**
     * Gets the reconciliationStatus value for this ProvideTotalsRequest.
     * 
     * @return reconciliationStatus
     */
    public int getReconciliationStatus() {
        return reconciliationStatus;
    }


    /**
     * Sets the reconciliationStatus value for this ProvideTotalsRequest.
     * 
     * @param reconciliationStatus
     */
    public void setReconciliationStatus(int reconciliationStatus) {
        this.reconciliationStatus = reconciliationStatus;
    }


    /**
     * Gets the unitType value for this ProvideTotalsRequest.
     * 
     * @return unitType
     */
    public java.lang.Integer getUnitType() {
        return unitType;
    }


    /**
     * Sets the unitType value for this ProvideTotalsRequest.
     * 
     * @param unitType
     */
    public void setUnitType(java.lang.Integer unitType) {
        this.unitType = unitType;
    }


    /**
     * Gets the suppUnitType value for this ProvideTotalsRequest.
     * 
     * @return suppUnitType
     */
    public java.lang.Integer getSuppUnitType() {
        return suppUnitType;
    }


    /**
     * Sets the suppUnitType value for this ProvideTotalsRequest.
     * 
     * @param suppUnitType
     */
    public void setSuppUnitType(java.lang.Integer suppUnitType) {
        this.suppUnitType = suppUnitType;
    }


    /**
     * Gets the accountCommitPeriod value for this ProvideTotalsRequest.
     * 
     * @return accountCommitPeriod
     */
    public java.lang.Integer getAccountCommitPeriod() {
        return accountCommitPeriod;
    }


    /**
     * Sets the accountCommitPeriod value for this ProvideTotalsRequest.
     * 
     * @param accountCommitPeriod
     */
    public void setAccountCommitPeriod(java.lang.Integer accountCommitPeriod) {
        this.accountCommitPeriod = accountCommitPeriod;
    }


    /**
     * Gets the accountType value for this ProvideTotalsRequest.
     * 
     * @return accountType
     */
    public java.lang.Integer getAccountType() {
        return accountType;
    }


    /**
     * Sets the accountType value for this ProvideTotalsRequest.
     * 
     * @param accountType
     */
    public void setAccountType(java.lang.Integer accountType) {
        this.accountType = accountType;
    }


    /**
     * Gets the byAccountFlag value for this ProvideTotalsRequest.
     * 
     * @return byAccountFlag
     */
    public java.lang.Integer getByAccountFlag() {
        return byAccountFlag;
    }


    /**
     * Sets the byAccountFlag value for this ProvideTotalsRequest.
     * 
     * @param byAccountFlag
     */
    public void setByAccountFlag(java.lang.Integer byAccountFlag) {
        this.byAccountFlag = byAccountFlag;
    }


    /**
     * Gets the responseCodes value for this ProvideTotalsRequest.
     * 
     * @return responseCodes
     */
    public int[] getResponseCodes() {
        return responseCodes;
    }


    /**
     * Sets the responseCodes value for this ProvideTotalsRequest.
     * 
     * @param responseCodes
     */
    public void setResponseCodes(int[] responseCodes) {
        this.responseCodes = responseCodes;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ProvideTotalsRequest)) return false;
        ProvideTotalsRequest other = (ProvideTotalsRequest) obj;
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
            ((this.reconciliationIdentifier==null && other.getReconciliationIdentifier()==null) || 
             (this.reconciliationIdentifier!=null &&
              this.reconciliationIdentifier.equals(other.getReconciliationIdentifier()))) &&
            ((this.reconciliationSnapshotDatetime==null && other.getReconciliationSnapshotDatetime()==null) || 
             (this.reconciliationSnapshotDatetime!=null &&
              this.reconciliationSnapshotDatetime.equals(other.getReconciliationSnapshotDatetime()))) &&
            this.reconciliationStatus == other.getReconciliationStatus() &&
            ((this.unitType==null && other.getUnitType()==null) || 
             (this.unitType!=null &&
              this.unitType.equals(other.getUnitType()))) &&
            ((this.suppUnitType==null && other.getSuppUnitType()==null) || 
             (this.suppUnitType!=null &&
              this.suppUnitType.equals(other.getSuppUnitType()))) &&
            ((this.accountCommitPeriod==null && other.getAccountCommitPeriod()==null) || 
             (this.accountCommitPeriod!=null &&
              this.accountCommitPeriod.equals(other.getAccountCommitPeriod()))) &&
            ((this.accountType==null && other.getAccountType()==null) || 
             (this.accountType!=null &&
              this.accountType.equals(other.getAccountType()))) &&
            ((this.byAccountFlag==null && other.getByAccountFlag()==null) || 
             (this.byAccountFlag!=null &&
              this.byAccountFlag.equals(other.getByAccountFlag()))) &&
            ((this.responseCodes==null && other.getResponseCodes()==null) || 
             (this.responseCodes!=null &&
              java.util.Arrays.equals(this.responseCodes, other.getResponseCodes())));
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
        if (getReconciliationIdentifier() != null) {
            _hashCode += getReconciliationIdentifier().hashCode();
        }
        if (getReconciliationSnapshotDatetime() != null) {
            _hashCode += getReconciliationSnapshotDatetime().hashCode();
        }
        _hashCode += getReconciliationStatus();
        if (getUnitType() != null) {
            _hashCode += getUnitType().hashCode();
        }
        if (getSuppUnitType() != null) {
            _hashCode += getSuppUnitType().hashCode();
        }
        if (getAccountCommitPeriod() != null) {
            _hashCode += getAccountCommitPeriod().hashCode();
        }
        if (getAccountType() != null) {
            _hashCode += getAccountType().hashCode();
        }
        if (getByAccountFlag() != null) {
            _hashCode += getByAccountFlag().hashCode();
        }
        if (getResponseCodes() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getResponseCodes());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getResponseCodes(), i);
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
