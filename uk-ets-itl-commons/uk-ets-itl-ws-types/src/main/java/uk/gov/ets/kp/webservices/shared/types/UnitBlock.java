/**
 * UnitBlock.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.gov.ets.kp.webservices.shared.types;

public class UnitBlock  implements java.io.Serializable {
    private long unitSerialBlockStart;

    private long unitSerialBlockEnd;

    private java.lang.String originatingRegistryCode;

    private int unitType;

    private java.lang.Integer suppUnitType;

    private int accountType;

    private java.lang.Long accountIdentifier;

    private int applicableCommitPeriod;

    public UnitBlock() {
    }

    public UnitBlock(
           long unitSerialBlockStart,
           long unitSerialBlockEnd,
           java.lang.String originatingRegistryCode,
           int unitType,
           java.lang.Integer suppUnitType,
           int accountType,
           java.lang.Long accountIdentifier,
           int applicableCommitPeriod) {
           this.unitSerialBlockStart = unitSerialBlockStart;
           this.unitSerialBlockEnd = unitSerialBlockEnd;
           this.originatingRegistryCode = originatingRegistryCode;
           this.unitType = unitType;
           this.suppUnitType = suppUnitType;
           this.accountType = accountType;
           this.accountIdentifier = accountIdentifier;
           this.applicableCommitPeriod = applicableCommitPeriod;
    }


    /**
     * Gets the unitSerialBlockStart value for this UnitBlock.
     * 
     * @return unitSerialBlockStart
     */
    public long getUnitSerialBlockStart() {
        return unitSerialBlockStart;
    }


    /**
     * Sets the unitSerialBlockStart value for this UnitBlock.
     * 
     * @param unitSerialBlockStart
     */
    public void setUnitSerialBlockStart(long unitSerialBlockStart) {
        this.unitSerialBlockStart = unitSerialBlockStart;
    }


    /**
     * Gets the unitSerialBlockEnd value for this UnitBlock.
     * 
     * @return unitSerialBlockEnd
     */
    public long getUnitSerialBlockEnd() {
        return unitSerialBlockEnd;
    }


    /**
     * Sets the unitSerialBlockEnd value for this UnitBlock.
     * 
     * @param unitSerialBlockEnd
     */
    public void setUnitSerialBlockEnd(long unitSerialBlockEnd) {
        this.unitSerialBlockEnd = unitSerialBlockEnd;
    }


    /**
     * Gets the originatingRegistryCode value for this UnitBlock.
     * 
     * @return originatingRegistryCode
     */
    public java.lang.String getOriginatingRegistryCode() {
        return originatingRegistryCode;
    }


    /**
     * Sets the originatingRegistryCode value for this UnitBlock.
     * 
     * @param originatingRegistryCode
     */
    public void setOriginatingRegistryCode(java.lang.String originatingRegistryCode) {
        this.originatingRegistryCode = originatingRegistryCode;
    }


    /**
     * Gets the unitType value for this UnitBlock.
     * 
     * @return unitType
     */
    public int getUnitType() {
        return unitType;
    }


    /**
     * Sets the unitType value for this UnitBlock.
     * 
     * @param unitType
     */
    public void setUnitType(int unitType) {
        this.unitType = unitType;
    }


    /**
     * Gets the suppUnitType value for this UnitBlock.
     * 
     * @return suppUnitType
     */
    public java.lang.Integer getSuppUnitType() {
        return suppUnitType;
    }


    /**
     * Sets the suppUnitType value for this UnitBlock.
     * 
     * @param suppUnitType
     */
    public void setSuppUnitType(java.lang.Integer suppUnitType) {
        this.suppUnitType = suppUnitType;
    }


    /**
     * Gets the accountType value for this UnitBlock.
     * 
     * @return accountType
     */
    public int getAccountType() {
        return accountType;
    }


    /**
     * Sets the accountType value for this UnitBlock.
     * 
     * @param accountType
     */
    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }


    /**
     * Gets the accountIdentifier value for this UnitBlock.
     * 
     * @return accountIdentifier
     */
    public java.lang.Long getAccountIdentifier() {
        return accountIdentifier;
    }


    /**
     * Sets the accountIdentifier value for this UnitBlock.
     * 
     * @param accountIdentifier
     */
    public void setAccountIdentifier(java.lang.Long accountIdentifier) {
        this.accountIdentifier = accountIdentifier;
    }


    /**
     * Gets the applicableCommitPeriod value for this UnitBlock.
     * 
     * @return applicableCommitPeriod
     */
    public int getApplicableCommitPeriod() {
        return applicableCommitPeriod;
    }


    /**
     * Sets the applicableCommitPeriod value for this UnitBlock.
     * 
     * @param applicableCommitPeriod
     */
    public void setApplicableCommitPeriod(int applicableCommitPeriod) {
        this.applicableCommitPeriod = applicableCommitPeriod;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UnitBlock)) return false;
        UnitBlock other = (UnitBlock) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.unitSerialBlockStart == other.getUnitSerialBlockStart() &&
            this.unitSerialBlockEnd == other.getUnitSerialBlockEnd() &&
            ((this.originatingRegistryCode==null && other.getOriginatingRegistryCode()==null) || 
             (this.originatingRegistryCode!=null &&
              this.originatingRegistryCode.equals(other.getOriginatingRegistryCode()))) &&
            this.unitType == other.getUnitType() &&
            ((this.suppUnitType==null && other.getSuppUnitType()==null) || 
             (this.suppUnitType!=null &&
              this.suppUnitType.equals(other.getSuppUnitType()))) &&
            this.accountType == other.getAccountType() &&
            ((this.accountIdentifier==null && other.getAccountIdentifier()==null) || 
             (this.accountIdentifier!=null &&
              this.accountIdentifier.equals(other.getAccountIdentifier()))) &&
            this.applicableCommitPeriod == other.getApplicableCommitPeriod();
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
        _hashCode += new Long(getUnitSerialBlockStart()).hashCode();
        _hashCode += new Long(getUnitSerialBlockEnd()).hashCode();
        if (getOriginatingRegistryCode() != null) {
            _hashCode += getOriginatingRegistryCode().hashCode();
        }
        _hashCode += getUnitType();
        if (getSuppUnitType() != null) {
            _hashCode += getSuppUnitType().hashCode();
        }
        _hashCode += getAccountType();
        if (getAccountIdentifier() != null) {
            _hashCode += getAccountIdentifier().hashCode();
        }
        _hashCode += getApplicableCommitPeriod();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(UnitBlock.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "UnitBlock"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unitSerialBlockStart");
        elemField.setXmlName(new javax.xml.namespace.QName("", "unitSerialBlockStart"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unitSerialBlockEnd");
        elemField.setXmlName(new javax.xml.namespace.QName("", "unitSerialBlockEnd"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("originatingRegistryCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "originatingRegistryCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unitType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "unitType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("suppUnitType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "suppUnitType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "accountType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "accountIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("applicableCommitPeriod");
        elemField.setXmlName(new javax.xml.namespace.QName("", "applicableCommitPeriod"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "UnitBlock{" +
                "unitSerialBlockStart=" + unitSerialBlockStart +
                ", unitSerialBlockEnd=" + unitSerialBlockEnd +
                ", originatingRegistryCode='" + originatingRegistryCode + '\'' +
                ", unitType=" + unitType +
                ", suppUnitType=" + suppUnitType +
                ", accountType=" + accountType +
                ", accountIdentifier=" + accountIdentifier +
                ", applicableCommitPeriod=" + applicableCommitPeriod +
                '}';
    }
}
