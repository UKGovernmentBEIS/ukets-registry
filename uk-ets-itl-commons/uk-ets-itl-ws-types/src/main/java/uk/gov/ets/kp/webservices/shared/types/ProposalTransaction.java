/**
 * ProposalTransaction.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.gov.ets.kp.webservices.shared.types;

public class ProposalTransaction  implements java.io.Serializable {
    private java.lang.String transactionIdentifier;

    private int transactionType;

    private java.lang.Integer suppTransactionType;

    private java.lang.String transferringRegistryCode;

    private java.lang.Integer transferringRegistryAccountType;

    private java.lang.Long transferringRegistryAccountIdentifier;

    private java.lang.String acquiringRegistryCode;

    private java.lang.Integer acquiringRegistryAccountType;

    private java.lang.Long acquiringRegistryAccountIdentifier;

    private java.lang.Long notificationIdentifier;

    private uk.gov.ets.kp.webservices.shared.types.TransactionUnitBlock[] proposalUnitBlocks;

    public ProposalTransaction() {
    }

    public ProposalTransaction(
           java.lang.String transactionIdentifier,
           int transactionType,
           java.lang.Integer suppTransactionType,
           java.lang.String transferringRegistryCode,
           java.lang.Integer transferringRegistryAccountType,
           java.lang.Long transferringRegistryAccountIdentifier,
           java.lang.String acquiringRegistryCode,
           java.lang.Integer acquiringRegistryAccountType,
           java.lang.Long acquiringRegistryAccountIdentifier,
           java.lang.Long notificationIdentifier,
           uk.gov.ets.kp.webservices.shared.types.TransactionUnitBlock[] proposalUnitBlocks) {
           this.transactionIdentifier = transactionIdentifier;
           this.transactionType = transactionType;
           this.suppTransactionType = suppTransactionType;
           this.transferringRegistryCode = transferringRegistryCode;
           this.transferringRegistryAccountType = transferringRegistryAccountType;
           this.transferringRegistryAccountIdentifier = transferringRegistryAccountIdentifier;
           this.acquiringRegistryCode = acquiringRegistryCode;
           this.acquiringRegistryAccountType = acquiringRegistryAccountType;
           this.acquiringRegistryAccountIdentifier = acquiringRegistryAccountIdentifier;
           this.notificationIdentifier = notificationIdentifier;
           this.proposalUnitBlocks = proposalUnitBlocks;
    }


    /**
     * Gets the transactionIdentifier value for this ProposalTransaction.
     * 
     * @return transactionIdentifier
     */
    public java.lang.String getTransactionIdentifier() {
        return transactionIdentifier;
    }


    /**
     * Sets the transactionIdentifier value for this ProposalTransaction.
     * 
     * @param transactionIdentifier
     */
    public void setTransactionIdentifier(java.lang.String transactionIdentifier) {
        this.transactionIdentifier = transactionIdentifier;
    }


    /**
     * Gets the transactionType value for this ProposalTransaction.
     * 
     * @return transactionType
     */
    public int getTransactionType() {
        return transactionType;
    }


    /**
     * Sets the transactionType value for this ProposalTransaction.
     * 
     * @param transactionType
     */
    public void setTransactionType(int transactionType) {
        this.transactionType = transactionType;
    }


    /**
     * Gets the suppTransactionType value for this ProposalTransaction.
     * 
     * @return suppTransactionType
     */
    public java.lang.Integer getSuppTransactionType() {
        return suppTransactionType;
    }


    /**
     * Sets the suppTransactionType value for this ProposalTransaction.
     * 
     * @param suppTransactionType
     */
    public void setSuppTransactionType(java.lang.Integer suppTransactionType) {
        this.suppTransactionType = suppTransactionType;
    }


    /**
     * Gets the transferringRegistryCode value for this ProposalTransaction.
     * 
     * @return transferringRegistryCode
     */
    public java.lang.String getTransferringRegistryCode() {
        return transferringRegistryCode;
    }


    /**
     * Sets the transferringRegistryCode value for this ProposalTransaction.
     * 
     * @param transferringRegistryCode
     */
    public void setTransferringRegistryCode(java.lang.String transferringRegistryCode) {
        this.transferringRegistryCode = transferringRegistryCode;
    }


    /**
     * Gets the transferringRegistryAccountType value for this ProposalTransaction.
     * 
     * @return transferringRegistryAccountType
     */
    public java.lang.Integer getTransferringRegistryAccountType() {
        return transferringRegistryAccountType;
    }


    /**
     * Sets the transferringRegistryAccountType value for this ProposalTransaction.
     * 
     * @param transferringRegistryAccountType
     */
    public void setTransferringRegistryAccountType(java.lang.Integer transferringRegistryAccountType) {
        this.transferringRegistryAccountType = transferringRegistryAccountType;
    }


    /**
     * Gets the transferringRegistryAccountIdentifier value for this ProposalTransaction.
     * 
     * @return transferringRegistryAccountIdentifier
     */
    public java.lang.Long getTransferringRegistryAccountIdentifier() {
        return transferringRegistryAccountIdentifier;
    }


    /**
     * Sets the transferringRegistryAccountIdentifier value for this ProposalTransaction.
     * 
     * @param transferringRegistryAccountIdentifier
     */
    public void setTransferringRegistryAccountIdentifier(java.lang.Long transferringRegistryAccountIdentifier) {
        this.transferringRegistryAccountIdentifier = transferringRegistryAccountIdentifier;
    }


    /**
     * Gets the acquiringRegistryCode value for this ProposalTransaction.
     * 
     * @return acquiringRegistryCode
     */
    public java.lang.String getAcquiringRegistryCode() {
        return acquiringRegistryCode;
    }


    /**
     * Sets the acquiringRegistryCode value for this ProposalTransaction.
     * 
     * @param acquiringRegistryCode
     */
    public void setAcquiringRegistryCode(java.lang.String acquiringRegistryCode) {
        this.acquiringRegistryCode = acquiringRegistryCode;
    }


    /**
     * Gets the acquiringRegistryAccountType value for this ProposalTransaction.
     * 
     * @return acquiringRegistryAccountType
     */
    public java.lang.Integer getAcquiringRegistryAccountType() {
        return acquiringRegistryAccountType;
    }


    /**
     * Sets the acquiringRegistryAccountType value for this ProposalTransaction.
     * 
     * @param acquiringRegistryAccountType
     */
    public void setAcquiringRegistryAccountType(java.lang.Integer acquiringRegistryAccountType) {
        this.acquiringRegistryAccountType = acquiringRegistryAccountType;
    }


    /**
     * Gets the acquiringRegistryAccountIdentifier value for this ProposalTransaction.
     * 
     * @return acquiringRegistryAccountIdentifier
     */
    public java.lang.Long getAcquiringRegistryAccountIdentifier() {
        return acquiringRegistryAccountIdentifier;
    }


    /**
     * Sets the acquiringRegistryAccountIdentifier value for this ProposalTransaction.
     * 
     * @param acquiringRegistryAccountIdentifier
     */
    public void setAcquiringRegistryAccountIdentifier(java.lang.Long acquiringRegistryAccountIdentifier) {
        this.acquiringRegistryAccountIdentifier = acquiringRegistryAccountIdentifier;
    }


    /**
     * Gets the notificationIdentifier value for this ProposalTransaction.
     * 
     * @return notificationIdentifier
     */
    public java.lang.Long getNotificationIdentifier() {
        return notificationIdentifier;
    }


    /**
     * Sets the notificationIdentifier value for this ProposalTransaction.
     * 
     * @param notificationIdentifier
     */
    public void setNotificationIdentifier(java.lang.Long notificationIdentifier) {
        this.notificationIdentifier = notificationIdentifier;
    }


    /**
     * Gets the proposalUnitBlocks value for this ProposalTransaction.
     * 
     * @return proposalUnitBlocks
     */
    public uk.gov.ets.kp.webservices.shared.types.TransactionUnitBlock[] getProposalUnitBlocks() {
        return proposalUnitBlocks;
    }


    /**
     * Sets the proposalUnitBlocks value for this ProposalTransaction.
     * 
     * @param proposalUnitBlocks
     */
    public void setProposalUnitBlocks(uk.gov.ets.kp.webservices.shared.types.TransactionUnitBlock[] proposalUnitBlocks) {
        this.proposalUnitBlocks = proposalUnitBlocks;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ProposalTransaction)) return false;
        ProposalTransaction other = (ProposalTransaction) obj;
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
            this.transactionType == other.getTransactionType() &&
            ((this.suppTransactionType==null && other.getSuppTransactionType()==null) || 
             (this.suppTransactionType!=null &&
              this.suppTransactionType.equals(other.getSuppTransactionType()))) &&
            ((this.transferringRegistryCode==null && other.getTransferringRegistryCode()==null) || 
             (this.transferringRegistryCode!=null &&
              this.transferringRegistryCode.equals(other.getTransferringRegistryCode()))) &&
            ((this.transferringRegistryAccountType==null && other.getTransferringRegistryAccountType()==null) || 
             (this.transferringRegistryAccountType!=null &&
              this.transferringRegistryAccountType.equals(other.getTransferringRegistryAccountType()))) &&
            ((this.transferringRegistryAccountIdentifier==null && other.getTransferringRegistryAccountIdentifier()==null) || 
             (this.transferringRegistryAccountIdentifier!=null &&
              this.transferringRegistryAccountIdentifier.equals(other.getTransferringRegistryAccountIdentifier()))) &&
            ((this.acquiringRegistryCode==null && other.getAcquiringRegistryCode()==null) || 
             (this.acquiringRegistryCode!=null &&
              this.acquiringRegistryCode.equals(other.getAcquiringRegistryCode()))) &&
            ((this.acquiringRegistryAccountType==null && other.getAcquiringRegistryAccountType()==null) || 
             (this.acquiringRegistryAccountType!=null &&
              this.acquiringRegistryAccountType.equals(other.getAcquiringRegistryAccountType()))) &&
            ((this.acquiringRegistryAccountIdentifier==null && other.getAcquiringRegistryAccountIdentifier()==null) || 
             (this.acquiringRegistryAccountIdentifier!=null &&
              this.acquiringRegistryAccountIdentifier.equals(other.getAcquiringRegistryAccountIdentifier()))) &&
            ((this.notificationIdentifier==null && other.getNotificationIdentifier()==null) || 
             (this.notificationIdentifier!=null &&
              this.notificationIdentifier.equals(other.getNotificationIdentifier()))) &&
            ((this.proposalUnitBlocks==null && other.getProposalUnitBlocks()==null) || 
             (this.proposalUnitBlocks!=null &&
              java.util.Arrays.equals(this.proposalUnitBlocks, other.getProposalUnitBlocks())));
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
        _hashCode += getTransactionType();
        if (getSuppTransactionType() != null) {
            _hashCode += getSuppTransactionType().hashCode();
        }
        if (getTransferringRegistryCode() != null) {
            _hashCode += getTransferringRegistryCode().hashCode();
        }
        if (getTransferringRegistryAccountType() != null) {
            _hashCode += getTransferringRegistryAccountType().hashCode();
        }
        if (getTransferringRegistryAccountIdentifier() != null) {
            _hashCode += getTransferringRegistryAccountIdentifier().hashCode();
        }
        if (getAcquiringRegistryCode() != null) {
            _hashCode += getAcquiringRegistryCode().hashCode();
        }
        if (getAcquiringRegistryAccountType() != null) {
            _hashCode += getAcquiringRegistryAccountType().hashCode();
        }
        if (getAcquiringRegistryAccountIdentifier() != null) {
            _hashCode += getAcquiringRegistryAccountIdentifier().hashCode();
        }
        if (getNotificationIdentifier() != null) {
            _hashCode += getNotificationIdentifier().hashCode();
        }
        if (getProposalUnitBlocks() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getProposalUnitBlocks());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getProposalUnitBlocks(), i);
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
        new org.apache.axis.description.TypeDesc(ProposalTransaction.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ProposalTransaction"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transactionIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transactionType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("suppTransactionType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "suppTransactionType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transferringRegistryCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transferringRegistryCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transferringRegistryAccountType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transferringRegistryAccountType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transferringRegistryAccountIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transferringRegistryAccountIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acquiringRegistryCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "acquiringRegistryCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acquiringRegistryAccountType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "acquiringRegistryAccountType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acquiringRegistryAccountIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "acquiringRegistryAccountIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("notificationIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "notificationIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("proposalUnitBlocks");
        elemField.setXmlName(new javax.xml.namespace.QName("", "proposalUnitBlocks"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "TransactionUnitBlock"));
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
        return "ProposalTransaction{" +
                "transactionIdentifier='" + transactionIdentifier + '\'' +
                ", transactionType=" + transactionType +
                ", suppTransactionType=" + suppTransactionType +
                ", transferringRegistryCode='" + transferringRegistryCode + '\'' +
                ", transferringRegistryAccountType=" + transferringRegistryAccountType +
                ", transferringRegistryAccountIdentifier=" + transferringRegistryAccountIdentifier +
                ", acquiringRegistryCode='" + acquiringRegistryCode + '\'' +
                ", acquiringRegistryAccountType=" + acquiringRegistryAccountType +
                ", acquiringRegistryAccountIdentifier=" + acquiringRegistryAccountIdentifier +
                ", notificationIdentifier=" + notificationIdentifier +
                ", proposalUnitBlocks=" + java.util.Arrays.toString(proposalUnitBlocks) +
                '}';
    }
}
