/**
 * ProvideAuditTrailRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.gov.ets.lib.commons.kyoto.types;

public class ProvideAuditTrailRequest  implements java.io.Serializable {
    private java.lang.String from;

    private java.lang.String to;

    private int majorVersion;

    private int minorVersion;

    private java.lang.String reconciliationIdentifier;

    private java.util.Calendar auditTrailBeginDatetime;

    private java.util.Calendar auditTrailEndDatetime;

    private java.lang.Integer accountType;

    private java.lang.Long accountIdentifier;

    private java.lang.Integer accountCommitPeriod;

    private java.lang.Integer unitType;

    private java.lang.Integer suppUnitType;

    private uk.gov.ets.lib.commons.kyoto.types.ReconciliationUnitBlockIdentifier[] unitBlockIdentifiers;

    private int[] responseCodes;

    public ProvideAuditTrailRequest() {
    }

    public ProvideAuditTrailRequest(
           java.lang.String from,
           java.lang.String to,
           int majorVersion,
           int minorVersion,
           java.lang.String reconciliationIdentifier,
           java.util.Calendar auditTrailBeginDatetime,
           java.util.Calendar auditTrailEndDatetime,
           java.lang.Integer accountType,
           java.lang.Long accountIdentifier,
           java.lang.Integer accountCommitPeriod,
           java.lang.Integer unitType,
           java.lang.Integer suppUnitType,
           uk.gov.ets.lib.commons.kyoto.types.ReconciliationUnitBlockIdentifier[] unitBlockIdentifiers,
           int[] responseCodes) {
           this.from = from;
           this.to = to;
           this.majorVersion = majorVersion;
           this.minorVersion = minorVersion;
           this.reconciliationIdentifier = reconciliationIdentifier;
           this.auditTrailBeginDatetime = auditTrailBeginDatetime;
           this.auditTrailEndDatetime = auditTrailEndDatetime;
           this.accountType = accountType;
           this.accountIdentifier = accountIdentifier;
           this.accountCommitPeriod = accountCommitPeriod;
           this.unitType = unitType;
           this.suppUnitType = suppUnitType;
           this.unitBlockIdentifiers = unitBlockIdentifiers;
           this.responseCodes = responseCodes;
    }


    /**
     * Gets the from value for this ProvideAuditTrailRequest.
     * 
     * @return from
     */
    public java.lang.String getFrom() {
        return from;
    }


    /**
     * Sets the from value for this ProvideAuditTrailRequest.
     * 
     * @param from
     */
    public void setFrom(java.lang.String from) {
        this.from = from;
    }


    /**
     * Gets the to value for this ProvideAuditTrailRequest.
     * 
     * @return to
     */
    public java.lang.String getTo() {
        return to;
    }


    /**
     * Sets the to value for this ProvideAuditTrailRequest.
     * 
     * @param to
     */
    public void setTo(java.lang.String to) {
        this.to = to;
    }


    /**
     * Gets the majorVersion value for this ProvideAuditTrailRequest.
     * 
     * @return majorVersion
     */
    public int getMajorVersion() {
        return majorVersion;
    }


    /**
     * Sets the majorVersion value for this ProvideAuditTrailRequest.
     * 
     * @param majorVersion
     */
    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }


    /**
     * Gets the minorVersion value for this ProvideAuditTrailRequest.
     * 
     * @return minorVersion
     */
    public int getMinorVersion() {
        return minorVersion;
    }


    /**
     * Sets the minorVersion value for this ProvideAuditTrailRequest.
     * 
     * @param minorVersion
     */
    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }


    /**
     * Gets the reconciliationIdentifier value for this ProvideAuditTrailRequest.
     * 
     * @return reconciliationIdentifier
     */
    public java.lang.String getReconciliationIdentifier() {
        return reconciliationIdentifier;
    }


    /**
     * Sets the reconciliationIdentifier value for this ProvideAuditTrailRequest.
     * 
     * @param reconciliationIdentifier
     */
    public void setReconciliationIdentifier(java.lang.String reconciliationIdentifier) {
        this.reconciliationIdentifier = reconciliationIdentifier;
    }


    /**
     * Gets the auditTrailBeginDatetime value for this ProvideAuditTrailRequest.
     * 
     * @return auditTrailBeginDatetime
     */
    public java.util.Calendar getAuditTrailBeginDatetime() {
        return auditTrailBeginDatetime;
    }


    /**
     * Sets the auditTrailBeginDatetime value for this ProvideAuditTrailRequest.
     * 
     * @param auditTrailBeginDatetime
     */
    public void setAuditTrailBeginDatetime(java.util.Calendar auditTrailBeginDatetime) {
        this.auditTrailBeginDatetime = auditTrailBeginDatetime;
    }


    /**
     * Gets the auditTrailEndDatetime value for this ProvideAuditTrailRequest.
     * 
     * @return auditTrailEndDatetime
     */
    public java.util.Calendar getAuditTrailEndDatetime() {
        return auditTrailEndDatetime;
    }


    /**
     * Sets the auditTrailEndDatetime value for this ProvideAuditTrailRequest.
     * 
     * @param auditTrailEndDatetime
     */
    public void setAuditTrailEndDatetime(java.util.Calendar auditTrailEndDatetime) {
        this.auditTrailEndDatetime = auditTrailEndDatetime;
    }


    /**
     * Gets the accountType value for this ProvideAuditTrailRequest.
     * 
     * @return accountType
     */
    public java.lang.Integer getAccountType() {
        return accountType;
    }


    /**
     * Sets the accountType value for this ProvideAuditTrailRequest.
     * 
     * @param accountType
     */
    public void setAccountType(java.lang.Integer accountType) {
        this.accountType = accountType;
    }


    /**
     * Gets the accountIdentifier value for this ProvideAuditTrailRequest.
     * 
     * @return accountIdentifier
     */
    public java.lang.Long getAccountIdentifier() {
        return accountIdentifier;
    }


    /**
     * Sets the accountIdentifier value for this ProvideAuditTrailRequest.
     * 
     * @param accountIdentifier
     */
    public void setAccountIdentifier(java.lang.Long accountIdentifier) {
        this.accountIdentifier = accountIdentifier;
    }


    /**
     * Gets the accountCommitPeriod value for this ProvideAuditTrailRequest.
     * 
     * @return accountCommitPeriod
     */
    public java.lang.Integer getAccountCommitPeriod() {
        return accountCommitPeriod;
    }


    /**
     * Sets the accountCommitPeriod value for this ProvideAuditTrailRequest.
     * 
     * @param accountCommitPeriod
     */
    public void setAccountCommitPeriod(java.lang.Integer accountCommitPeriod) {
        this.accountCommitPeriod = accountCommitPeriod;
    }


    /**
     * Gets the unitType value for this ProvideAuditTrailRequest.
     * 
     * @return unitType
     */
    public java.lang.Integer getUnitType() {
        return unitType;
    }


    /**
     * Sets the unitType value for this ProvideAuditTrailRequest.
     * 
     * @param unitType
     */
    public void setUnitType(java.lang.Integer unitType) {
        this.unitType = unitType;
    }


    /**
     * Gets the suppUnitType value for this ProvideAuditTrailRequest.
     * 
     * @return suppUnitType
     */
    public java.lang.Integer getSuppUnitType() {
        return suppUnitType;
    }


    /**
     * Sets the suppUnitType value for this ProvideAuditTrailRequest.
     * 
     * @param suppUnitType
     */
    public void setSuppUnitType(java.lang.Integer suppUnitType) {
        this.suppUnitType = suppUnitType;
    }


    /**
     * Gets the unitBlockIdentifiers value for this ProvideAuditTrailRequest.
     * 
     * @return unitBlockIdentifiers
     */
    public uk.gov.ets.lib.commons.kyoto.types.ReconciliationUnitBlockIdentifier[] getUnitBlockIdentifiers() {
        return unitBlockIdentifiers;
    }


    /**
     * Sets the unitBlockIdentifiers value for this ProvideAuditTrailRequest.
     * 
     * @param unitBlockIdentifiers
     */
    public void setUnitBlockIdentifiers(uk.gov.ets.lib.commons.kyoto.types.ReconciliationUnitBlockIdentifier[] unitBlockIdentifiers) {
        this.unitBlockIdentifiers = unitBlockIdentifiers;
    }


    /**
     * Gets the responseCodes value for this ProvideAuditTrailRequest.
     * 
     * @return responseCodes
     */
    public int[] getResponseCodes() {
        return responseCodes;
    }


    /**
     * Sets the responseCodes value for this ProvideAuditTrailRequest.
     * 
     * @param responseCodes
     */
    public void setResponseCodes(int[] responseCodes) {
        this.responseCodes = responseCodes;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ProvideAuditTrailRequest)) return false;
        ProvideAuditTrailRequest other = (ProvideAuditTrailRequest) obj;
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
            ((this.auditTrailBeginDatetime==null && other.getAuditTrailBeginDatetime()==null) || 
             (this.auditTrailBeginDatetime!=null &&
              this.auditTrailBeginDatetime.equals(other.getAuditTrailBeginDatetime()))) &&
            ((this.auditTrailEndDatetime==null && other.getAuditTrailEndDatetime()==null) || 
             (this.auditTrailEndDatetime!=null &&
              this.auditTrailEndDatetime.equals(other.getAuditTrailEndDatetime()))) &&
            ((this.accountType==null && other.getAccountType()==null) || 
             (this.accountType!=null &&
              this.accountType.equals(other.getAccountType()))) &&
            ((this.accountIdentifier==null && other.getAccountIdentifier()==null) || 
             (this.accountIdentifier!=null &&
              this.accountIdentifier.equals(other.getAccountIdentifier()))) &&
            ((this.accountCommitPeriod==null && other.getAccountCommitPeriod()==null) || 
             (this.accountCommitPeriod!=null &&
              this.accountCommitPeriod.equals(other.getAccountCommitPeriod()))) &&
            ((this.unitType==null && other.getUnitType()==null) || 
             (this.unitType!=null &&
              this.unitType.equals(other.getUnitType()))) &&
            ((this.suppUnitType==null && other.getSuppUnitType()==null) || 
             (this.suppUnitType!=null &&
              this.suppUnitType.equals(other.getSuppUnitType()))) &&
            ((this.unitBlockIdentifiers==null && other.getUnitBlockIdentifiers()==null) || 
             (this.unitBlockIdentifiers!=null &&
              java.util.Arrays.equals(this.unitBlockIdentifiers, other.getUnitBlockIdentifiers()))) &&
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
        if (getAuditTrailBeginDatetime() != null) {
            _hashCode += getAuditTrailBeginDatetime().hashCode();
        }
        if (getAuditTrailEndDatetime() != null) {
            _hashCode += getAuditTrailEndDatetime().hashCode();
        }
        if (getAccountType() != null) {
            _hashCode += getAccountType().hashCode();
        }
        if (getAccountIdentifier() != null) {
            _hashCode += getAccountIdentifier().hashCode();
        }
        if (getAccountCommitPeriod() != null) {
            _hashCode += getAccountCommitPeriod().hashCode();
        }
        if (getUnitType() != null) {
            _hashCode += getUnitType().hashCode();
        }
        if (getSuppUnitType() != null) {
            _hashCode += getSuppUnitType().hashCode();
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
