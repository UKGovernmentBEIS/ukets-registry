/**
 * TransactionStatusResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.gov.ets.kp.webservices.shared.types;

public class TransactionStatusResponse  implements java.io.Serializable {
    private java.lang.String transactionIdentifier;

    private int transactionStatus;

    private java.util.Calendar transactionStatusDateTime;

    private int resultIdentifier;

    private int[] responseCodes;

    public TransactionStatusResponse() {
    }

    public TransactionStatusResponse(
           java.lang.String transactionIdentifier,
           int transactionStatus,
           java.util.Calendar transactionStatusDateTime,
           int resultIdentifier,
           int[] responseCodes) {
           this.transactionIdentifier = transactionIdentifier;
           this.transactionStatus = transactionStatus;
           this.transactionStatusDateTime = transactionStatusDateTime;
           this.resultIdentifier = resultIdentifier;
           this.responseCodes = responseCodes;
    }


    /**
     * Gets the transactionIdentifier value for this TransactionStatusResponse.
     * 
     * @return transactionIdentifier
     */
    public java.lang.String getTransactionIdentifier() {
        return transactionIdentifier;
    }


    /**
     * Sets the transactionIdentifier value for this TransactionStatusResponse.
     * 
     * @param transactionIdentifier
     */
    public void setTransactionIdentifier(java.lang.String transactionIdentifier) {
        this.transactionIdentifier = transactionIdentifier;
    }


    /**
     * Gets the transactionStatus value for this TransactionStatusResponse.
     * 
     * @return transactionStatus
     */
    public int getTransactionStatus() {
        return transactionStatus;
    }


    /**
     * Sets the transactionStatus value for this TransactionStatusResponse.
     * 
     * @param transactionStatus
     */
    public void setTransactionStatus(int transactionStatus) {
        this.transactionStatus = transactionStatus;
    }


    /**
     * Gets the transactionStatusDateTime value for this TransactionStatusResponse.
     * 
     * @return transactionStatusDateTime
     */
    public java.util.Calendar getTransactionStatusDateTime() {
        return transactionStatusDateTime;
    }


    /**
     * Sets the transactionStatusDateTime value for this TransactionStatusResponse.
     * 
     * @param transactionStatusDateTime
     */
    public void setTransactionStatusDateTime(java.util.Calendar transactionStatusDateTime) {
        this.transactionStatusDateTime = transactionStatusDateTime;
    }


    /**
     * Gets the resultIdentifier value for this TransactionStatusResponse.
     * 
     * @return resultIdentifier
     */
    public int getResultIdentifier() {
        return resultIdentifier;
    }


    /**
     * Sets the resultIdentifier value for this TransactionStatusResponse.
     * 
     * @param resultIdentifier
     */
    public void setResultIdentifier(int resultIdentifier) {
        this.resultIdentifier = resultIdentifier;
    }


    /**
     * Gets the responseCodes value for this TransactionStatusResponse.
     * 
     * @return responseCodes
     */
    public int[] getResponseCodes() {
        return responseCodes;
    }


    /**
     * Sets the responseCodes value for this TransactionStatusResponse.
     * 
     * @param responseCodes
     */
    public void setResponseCodes(int[] responseCodes) {
        this.responseCodes = responseCodes;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TransactionStatusResponse)) return false;
        TransactionStatusResponse other = (TransactionStatusResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.transactionIdentifier==null && other.getTransactionIdentifier()==null) || 
             (this.transactionIdentifier!=null &&
              this.transactionIdentifier.equals(other.getTransactionIdentifier()))) &&
            this.transactionStatus == other.getTransactionStatus() &&
            ((this.transactionStatusDateTime==null && other.getTransactionStatusDateTime()==null) || 
             (this.transactionStatusDateTime!=null &&
              this.transactionStatusDateTime.equals(other.getTransactionStatusDateTime()))) &&
            this.resultIdentifier == other.getResultIdentifier() &&
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
        if (getTransactionIdentifier() != null) {
            _hashCode += getTransactionIdentifier().hashCode();
        }
        _hashCode += getTransactionStatus();
        if (getTransactionStatusDateTime() != null) {
            _hashCode += getTransactionStatusDateTime().hashCode();
        }
        _hashCode += getResultIdentifier();
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

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TransactionStatusResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "TransactionStatusResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transactionIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transactionStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionStatusDateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transactionStatusDateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "resultIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("responseCodes");
        elemField.setXmlName(new javax.xml.namespace.QName("", "responseCodes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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

}
