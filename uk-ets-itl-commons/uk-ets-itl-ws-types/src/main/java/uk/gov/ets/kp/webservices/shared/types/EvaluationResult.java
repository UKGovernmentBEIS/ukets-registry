/**
 * EvaluationResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.gov.ets.kp.webservices.shared.types;

public class EvaluationResult  implements java.io.Serializable {
    private int responseCode;

    private uk.gov.ets.kp.webservices.shared.types.UnitBlockIdentifier[] unitBlockIdentifiers;

    public EvaluationResult() {
    }

    public EvaluationResult(
           int responseCode,
           uk.gov.ets.kp.webservices.shared.types.UnitBlockIdentifier[] unitBlockIdentifiers) {
           this.responseCode = responseCode;
           this.unitBlockIdentifiers = unitBlockIdentifiers;
    }


    /**
     * Gets the responseCode value for this EvaluationResult.
     * 
     * @return responseCode
     */
    public int getResponseCode() {
        return responseCode;
    }


    /**
     * Sets the responseCode value for this EvaluationResult.
     * 
     * @param responseCode
     */
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }


    /**
     * Gets the unitBlockIdentifiers value for this EvaluationResult.
     * 
     * @return unitBlockIdentifiers
     */
    public uk.gov.ets.kp.webservices.shared.types.UnitBlockIdentifier[] getUnitBlockIdentifiers() {
        return unitBlockIdentifiers;
    }


    /**
     * Sets the unitBlockIdentifiers value for this EvaluationResult.
     * 
     * @param unitBlockIdentifiers
     */
    public void setUnitBlockIdentifiers(uk.gov.ets.kp.webservices.shared.types.UnitBlockIdentifier[] unitBlockIdentifiers) {
        this.unitBlockIdentifiers = unitBlockIdentifiers;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EvaluationResult)) return false;
        EvaluationResult other = (EvaluationResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.responseCode == other.getResponseCode() &&
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
        _hashCode += getResponseCode();
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

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EvaluationResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "EvaluationResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("responseCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "responseCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unitBlockIdentifiers");
        elemField.setXmlName(new javax.xml.namespace.QName("", "unitBlockIdentifiers"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "UnitBlockIdentifier"));
        elemField.setMinOccurs(0);
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
        return "EvaluationResult{" +
                "responseCode=" + responseCode +
                ", unitBlockIdentifiers=" + java.util.Arrays.toString(unitBlockIdentifiers) +
                '}';
    }
}
