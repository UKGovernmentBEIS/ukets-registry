/**
 * UnitBlockIdentifier.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.gov.ets.kp.webservices.shared.types;

public class UnitBlockIdentifier  implements java.io.Serializable {
    private long unitSerialBlockStart;

    private long unitSerialBlockEnd;

    private java.lang.String originatingRegistryCode;

    public UnitBlockIdentifier() {
    }

    public UnitBlockIdentifier(
           long unitSerialBlockStart,
           long unitSerialBlockEnd,
           java.lang.String originatingRegistryCode) {
           this.unitSerialBlockStart = unitSerialBlockStart;
           this.unitSerialBlockEnd = unitSerialBlockEnd;
           this.originatingRegistryCode = originatingRegistryCode;
    }


    /**
     * Gets the unitSerialBlockStart value for this UnitBlockIdentifier.
     * 
     * @return unitSerialBlockStart
     */
    public long getUnitSerialBlockStart() {
        return unitSerialBlockStart;
    }


    /**
     * Sets the unitSerialBlockStart value for this UnitBlockIdentifier.
     * 
     * @param unitSerialBlockStart
     */
    public void setUnitSerialBlockStart(long unitSerialBlockStart) {
        this.unitSerialBlockStart = unitSerialBlockStart;
    }


    /**
     * Gets the unitSerialBlockEnd value for this UnitBlockIdentifier.
     * 
     * @return unitSerialBlockEnd
     */
    public long getUnitSerialBlockEnd() {
        return unitSerialBlockEnd;
    }


    /**
     * Sets the unitSerialBlockEnd value for this UnitBlockIdentifier.
     * 
     * @param unitSerialBlockEnd
     */
    public void setUnitSerialBlockEnd(long unitSerialBlockEnd) {
        this.unitSerialBlockEnd = unitSerialBlockEnd;
    }


    /**
     * Gets the originatingRegistryCode value for this UnitBlockIdentifier.
     * 
     * @return originatingRegistryCode
     */
    public java.lang.String getOriginatingRegistryCode() {
        return originatingRegistryCode;
    }


    /**
     * Sets the originatingRegistryCode value for this UnitBlockIdentifier.
     * 
     * @param originatingRegistryCode
     */
    public void setOriginatingRegistryCode(java.lang.String originatingRegistryCode) {
        this.originatingRegistryCode = originatingRegistryCode;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UnitBlockIdentifier)) return false;
        UnitBlockIdentifier other = (UnitBlockIdentifier) obj;
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
              this.originatingRegistryCode.equals(other.getOriginatingRegistryCode())));
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
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(UnitBlockIdentifier.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "UnitBlockIdentifier"));
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
        return "UnitBlockIdentifier{" +
                "unitSerialBlockStart=" + unitSerialBlockStart +
                ", unitSerialBlockEnd=" + unitSerialBlockEnd +
                ", originatingRegistryCode='" + originatingRegistryCode + '\'' +
                '}';
    }
}
