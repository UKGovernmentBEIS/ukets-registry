/**
 * Total.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.gov.ets.kp.webservices.shared.types;

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

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Total.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "Total"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
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
        elemField.setFieldName("accountCommitPeriod");
        elemField.setXmlName(new javax.xml.namespace.QName("", "accountCommitPeriod"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
        elemField.setFieldName("unitCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "unitCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
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

}
