/**
 * UnitBlockRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.gov.ets.lib.commons.kyoto.types;

public class UnitBlockRequest  implements java.io.Serializable {
    private java.lang.Integer unitType;

    private java.lang.Integer suppUnitType;

    private java.lang.Integer accountCommitPeriod;

    private java.lang.Integer accountType;

    private java.lang.Long accountIdentifier;

    public UnitBlockRequest() {
    }

    public UnitBlockRequest(
           java.lang.Integer unitType,
           java.lang.Integer suppUnitType,
           java.lang.Integer accountCommitPeriod,
           java.lang.Integer accountType,
           java.lang.Long accountIdentifier) {
           this.unitType = unitType;
           this.suppUnitType = suppUnitType;
           this.accountCommitPeriod = accountCommitPeriod;
           this.accountType = accountType;
           this.accountIdentifier = accountIdentifier;
    }


    /**
     * Gets the unitType value for this UnitBlockRequest.
     * 
     * @return unitType
     */
    public java.lang.Integer getUnitType() {
        return unitType;
    }


    /**
     * Sets the unitType value for this UnitBlockRequest.
     * 
     * @param unitType
     */
    public void setUnitType(java.lang.Integer unitType) {
        this.unitType = unitType;
    }


    /**
     * Gets the suppUnitType value for this UnitBlockRequest.
     * 
     * @return suppUnitType
     */
    public java.lang.Integer getSuppUnitType() {
        return suppUnitType;
    }


    /**
     * Sets the suppUnitType value for this UnitBlockRequest.
     * 
     * @param suppUnitType
     */
    public void setSuppUnitType(java.lang.Integer suppUnitType) {
        this.suppUnitType = suppUnitType;
    }


    /**
     * Gets the accountCommitPeriod value for this UnitBlockRequest.
     * 
     * @return accountCommitPeriod
     */
    public java.lang.Integer getAccountCommitPeriod() {
        return accountCommitPeriod;
    }


    /**
     * Sets the accountCommitPeriod value for this UnitBlockRequest.
     * 
     * @param accountCommitPeriod
     */
    public void setAccountCommitPeriod(java.lang.Integer accountCommitPeriod) {
        this.accountCommitPeriod = accountCommitPeriod;
    }


    /**
     * Gets the accountType value for this UnitBlockRequest.
     * 
     * @return accountType
     */
    public java.lang.Integer getAccountType() {
        return accountType;
    }


    /**
     * Sets the accountType value for this UnitBlockRequest.
     * 
     * @param accountType
     */
    public void setAccountType(java.lang.Integer accountType) {
        this.accountType = accountType;
    }


    /**
     * Gets the accountIdentifier value for this UnitBlockRequest.
     * 
     * @return accountIdentifier
     */
    public java.lang.Long getAccountIdentifier() {
        return accountIdentifier;
    }


    /**
     * Sets the accountIdentifier value for this UnitBlockRequest.
     * 
     * @param accountIdentifier
     */
    public void setAccountIdentifier(java.lang.Long accountIdentifier) {
        this.accountIdentifier = accountIdentifier;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UnitBlockRequest)) return false;
        UnitBlockRequest other = (UnitBlockRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
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
            ((this.accountIdentifier==null && other.getAccountIdentifier()==null) || 
             (this.accountIdentifier!=null &&
              this.accountIdentifier.equals(other.getAccountIdentifier())));
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
        if (getAccountIdentifier() != null) {
            _hashCode += getAccountIdentifier().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }
}
