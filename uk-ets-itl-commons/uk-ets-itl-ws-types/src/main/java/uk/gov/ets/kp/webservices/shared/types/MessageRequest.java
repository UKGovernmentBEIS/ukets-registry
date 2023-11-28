/**
 * MessageRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.gov.ets.kp.webservices.shared.types;

public class MessageRequest  implements java.io.Serializable {
    private java.lang.String from;

    private java.lang.String to;

    private int majorVersion;

    private int minorVersion;

    private java.lang.String messageContent;

    private java.util.Calendar messageDateTime;

    public MessageRequest() {
    }

    public MessageRequest(
           java.lang.String from,
           java.lang.String to,
           int majorVersion,
           int minorVersion,
           java.lang.String messageContent,
           java.util.Calendar messageDateTime) {
           this.from = from;
           this.to = to;
           this.majorVersion = majorVersion;
           this.minorVersion = minorVersion;
           this.messageContent = messageContent;
           this.messageDateTime = messageDateTime;
    }


    /**
     * Gets the from value for this MessageRequest.
     * 
     * @return from
     */
    public java.lang.String getFrom() {
        return from;
    }


    /**
     * Sets the from value for this MessageRequest.
     * 
     * @param from
     */
    public void setFrom(java.lang.String from) {
        this.from = from;
    }


    /**
     * Gets the to value for this MessageRequest.
     * 
     * @return to
     */
    public java.lang.String getTo() {
        return to;
    }


    /**
     * Sets the to value for this MessageRequest.
     * 
     * @param to
     */
    public void setTo(java.lang.String to) {
        this.to = to;
    }


    /**
     * Gets the majorVersion value for this MessageRequest.
     * 
     * @return majorVersion
     */
    public int getMajorVersion() {
        return majorVersion;
    }


    /**
     * Sets the majorVersion value for this MessageRequest.
     * 
     * @param majorVersion
     */
    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }


    /**
     * Gets the minorVersion value for this MessageRequest.
     * 
     * @return minorVersion
     */
    public int getMinorVersion() {
        return minorVersion;
    }


    /**
     * Sets the minorVersion value for this MessageRequest.
     * 
     * @param minorVersion
     */
    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }


    /**
     * Gets the messageContent value for this MessageRequest.
     * 
     * @return messageContent
     */
    public java.lang.String getMessageContent() {
        return messageContent;
    }


    /**
     * Sets the messageContent value for this MessageRequest.
     * 
     * @param messageContent
     */
    public void setMessageContent(java.lang.String messageContent) {
        this.messageContent = messageContent;
    }


    /**
     * Gets the messageDateTime value for this MessageRequest.
     * 
     * @return messageDateTime
     */
    public java.util.Calendar getMessageDateTime() {
        return messageDateTime;
    }


    /**
     * Sets the messageDateTime value for this MessageRequest.
     * 
     * @param messageDateTime
     */
    public void setMessageDateTime(java.util.Calendar messageDateTime) {
        this.messageDateTime = messageDateTime;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MessageRequest)) return false;
        MessageRequest other = (MessageRequest) obj;
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
            ((this.messageContent==null && other.getMessageContent()==null) || 
             (this.messageContent!=null &&
              this.messageContent.equals(other.getMessageContent()))) &&
            ((this.messageDateTime==null && other.getMessageDateTime()==null) || 
             (this.messageDateTime!=null &&
              this.messageDateTime.equals(other.getMessageDateTime())));
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
        if (getMessageContent() != null) {
            _hashCode += getMessageContent().hashCode();
        }
        if (getMessageDateTime() != null) {
            _hashCode += getMessageDateTime().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MessageRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "MessageRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("from");
        elemField.setXmlName(new javax.xml.namespace.QName("", "from"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("to");
        elemField.setXmlName(new javax.xml.namespace.QName("", "to"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("majorVersion");
        elemField.setXmlName(new javax.xml.namespace.QName("", "majorVersion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("minorVersion");
        elemField.setXmlName(new javax.xml.namespace.QName("", "minorVersion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("messageContent");
        elemField.setXmlName(new javax.xml.namespace.QName("", "messageContent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("messageDateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "messageDateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
