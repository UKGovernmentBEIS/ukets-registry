/**
 * Total.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.gov.ets.lib.commons.kyoto.types;

public class Total  implements java.io.Serializable {
    private int accountType;

    private java.lang.Long accountIdentifier;

    private int accountCommitPeriod;

    private int unitType;

    private java.lang.Integer suppUnitType;

    private long unitCount;

    public Total() {
    }

    public Total(
           int accountType,
           java.lang.Long accountIdentifier,
           int accountCommitPeriod,
           int unitType,
           java.lang.Integer suppUnitType,
           long unitCount) {
           this.accountType = accountType;
           this.accountIdentifier = accountIdentifier;
           this.accountCommitPeriod = accountCommitPeriod;
           this.unitType = unitType;
           this.suppUnitType = suppUnitType;
           this.unitCount = unitCount;
    }


    /**
     * Gets the accountType value for this Total.
     * 
     * @return accountType
     */
    public int getAccountType() {
        return accountType;
    }


    /**
     * Sets the accountType value for this Total.
     * 
     * @param accountType
     */
    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }


    /**
     * Gets the accountIdentifier value for this Total.
     * 
     * @return accountIdentifier
     */
    public java.lang.Long getAccountIdentifier() {
        return accountIdentifier;
    }


    /**
     * Sets the accountIdentifier value for this Total.
     * 
     * @param accountIdentifier
     */
    public void setAccountIdentifier(java.lang.Long accountIdentifier) {
        this.accountIdentifier = accountIdentifier;
    }


    /**
     * Gets the accountCommitPeriod value for this Total.
     * 
     * @return accountCommitPeriod
     */
    public int getAccountCommitPeriod() {
        return accountCommitPeriod;
    }


    /**
     * Sets the accountCommitPeriod value for this Total.
     * 
     * @param accountCommitPeriod
     */
    public void setAccountCommitPeriod(int accountCommitPeriod) {
        this.accountCommitPeriod = accountCommitPeriod;
    }


    /**
     * Gets the unitType value for this Total.
     * 
     * @return unitType
     */
    public int getUnitType() {
        return unitType;
    }


    /**
     * Sets the unitType value for this Total.
     * 
     * @param unitType
     */
    public void setUnitType(int unitType) {
        this.unitType = unitType;
    }


    /**
     * Gets the suppUnitType value for this Total.
     * 
     * @return suppUnitType
     */
    public java.lang.Integer getSuppUnitType() {
        return suppUnitType;
    }


    /**
     * Sets the suppUnitType value for this Total.
     * 
     * @param suppUnitType
     */
    public void setSuppUnitType(java.lang.Integer suppUnitType) {
        this.suppUnitType = suppUnitType;
    }


    /**
     * Gets the unitCount value for this Total.
     * 
     * @return unitCount
     */
    public long getUnitCount() {
        return unitCount;
    }


    /**
     * Sets the unitCount value for this Total.
     * 
     * @param unitCount
     */
    public void setUnitCount(long unitCount) {
        this.unitCount = unitCount;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Total)) return false;
        Total other = (Total) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.accountType == other.getAccountType() &&
            ((this.accountIdentifier==null && other.getAccountIdentifier()==null) || 
             (this.accountIdentifier!=null &&
              this.accountIdentifier.equals(other.getAccountIdentifier()))) &&
            this.accountCommitPeriod == other.getAccountCommitPeriod() &&
            this.unitType == other.getUnitType() &&
            ((this.suppUnitType==null && other.getSuppUnitType()==null) || 
             (this.suppUnitType!=null &&
              this.suppUnitType.equals(other.getSuppUnitType()))) &&
            this.unitCount == other.getUnitCount();
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
        _hashCode += getAccountType();
        if (getAccountIdentifier() != null) {
            _hashCode += getAccountIdentifier().hashCode();
        }
        _hashCode += getAccountCommitPeriod();
        _hashCode += getUnitType();
        if (getSuppUnitType() != null) {
            _hashCode += getSuppUnitType().hashCode();
        }
        _hashCode += new Long(getUnitCount()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }
}
