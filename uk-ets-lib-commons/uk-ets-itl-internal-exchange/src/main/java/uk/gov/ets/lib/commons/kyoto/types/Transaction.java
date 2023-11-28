/**
 * Transaction.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.gov.ets.lib.commons.kyoto.types;

public class Transaction  implements java.io.Serializable {
    private java.lang.String transactionIdentifier;

    private int transactionType;

    private java.lang.Integer suppTransactionType;

    private java.util.Calendar transactionStatusDateTime;

    private java.lang.String transferringRegistryCode;

    private java.lang.Integer transferringRegistryAccountType;

    private java.lang.Long transferringRegistryAccountIdentifier;

    private java.lang.String acquiringRegistryCode;

    private java.lang.Integer acquiringRegistryAccountType;

    private java.lang.Long acquiringRegistryAccountIdentifier;

    private java.lang.Long notificationIdentifier;

    private uk.gov.ets.lib.commons.kyoto.types.ReconciliationTransactionUnitBlock[] transactionBlocks;

    public Transaction() {
    }

    public Transaction(
           java.lang.String transactionIdentifier,
           int transactionType,
           java.lang.Integer suppTransactionType,
           java.util.Calendar transactionStatusDateTime,
           java.lang.String transferringRegistryCode,
           java.lang.Integer transferringRegistryAccountType,
           java.lang.Long transferringRegistryAccountIdentifier,
           java.lang.String acquiringRegistryCode,
           java.lang.Integer acquiringRegistryAccountType,
           java.lang.Long acquiringRegistryAccountIdentifier,
           java.lang.Long notificationIdentifier,
           uk.gov.ets.lib.commons.kyoto.types.ReconciliationTransactionUnitBlock[] transactionBlocks) {
           this.transactionIdentifier = transactionIdentifier;
           this.transactionType = transactionType;
           this.suppTransactionType = suppTransactionType;
           this.transactionStatusDateTime = transactionStatusDateTime;
           this.transferringRegistryCode = transferringRegistryCode;
           this.transferringRegistryAccountType = transferringRegistryAccountType;
           this.transferringRegistryAccountIdentifier = transferringRegistryAccountIdentifier;
           this.acquiringRegistryCode = acquiringRegistryCode;
           this.acquiringRegistryAccountType = acquiringRegistryAccountType;
           this.acquiringRegistryAccountIdentifier = acquiringRegistryAccountIdentifier;
           this.notificationIdentifier = notificationIdentifier;
           this.transactionBlocks = transactionBlocks;
    }


    /**
     * Gets the transactionIdentifier value for this Transaction.
     * 
     * @return transactionIdentifier
     */
    public java.lang.String getTransactionIdentifier() {
        return transactionIdentifier;
    }


    /**
     * Sets the transactionIdentifier value for this Transaction.
     * 
     * @param transactionIdentifier
     */
    public void setTransactionIdentifier(java.lang.String transactionIdentifier) {
        this.transactionIdentifier = transactionIdentifier;
    }


    /**
     * Gets the transactionType value for this Transaction.
     * 
     * @return transactionType
     */
    public int getTransactionType() {
        return transactionType;
    }


    /**
     * Sets the transactionType value for this Transaction.
     * 
     * @param transactionType
     */
    public void setTransactionType(int transactionType) {
        this.transactionType = transactionType;
    }


    /**
     * Gets the suppTransactionType value for this Transaction.
     * 
     * @return suppTransactionType
     */
    public java.lang.Integer getSuppTransactionType() {
        return suppTransactionType;
    }


    /**
     * Sets the suppTransactionType value for this Transaction.
     * 
     * @param suppTransactionType
     */
    public void setSuppTransactionType(java.lang.Integer suppTransactionType) {
        this.suppTransactionType = suppTransactionType;
    }


    /**
     * Gets the transactionStatusDateTime value for this Transaction.
     * 
     * @return transactionStatusDateTime
     */
    public java.util.Calendar getTransactionStatusDateTime() {
        return transactionStatusDateTime;
    }


    /**
     * Sets the transactionStatusDateTime value for this Transaction.
     * 
     * @param transactionStatusDateTime
     */
    public void setTransactionStatusDateTime(java.util.Calendar transactionStatusDateTime) {
        this.transactionStatusDateTime = transactionStatusDateTime;
    }


    /**
     * Gets the transferringRegistryCode value for this Transaction.
     * 
     * @return transferringRegistryCode
     */
    public java.lang.String getTransferringRegistryCode() {
        return transferringRegistryCode;
    }


    /**
     * Sets the transferringRegistryCode value for this Transaction.
     * 
     * @param transferringRegistryCode
     */
    public void setTransferringRegistryCode(java.lang.String transferringRegistryCode) {
        this.transferringRegistryCode = transferringRegistryCode;
    }


    /**
     * Gets the transferringRegistryAccountType value for this Transaction.
     * 
     * @return transferringRegistryAccountType
     */
    public java.lang.Integer getTransferringRegistryAccountType() {
        return transferringRegistryAccountType;
    }


    /**
     * Sets the transferringRegistryAccountType value for this Transaction.
     * 
     * @param transferringRegistryAccountType
     */
    public void setTransferringRegistryAccountType(java.lang.Integer transferringRegistryAccountType) {
        this.transferringRegistryAccountType = transferringRegistryAccountType;
    }


    /**
     * Gets the transferringRegistryAccountIdentifier value for this Transaction.
     * 
     * @return transferringRegistryAccountIdentifier
     */
    public java.lang.Long getTransferringRegistryAccountIdentifier() {
        return transferringRegistryAccountIdentifier;
    }


    /**
     * Sets the transferringRegistryAccountIdentifier value for this Transaction.
     * 
     * @param transferringRegistryAccountIdentifier
     */
    public void setTransferringRegistryAccountIdentifier(java.lang.Long transferringRegistryAccountIdentifier) {
        this.transferringRegistryAccountIdentifier = transferringRegistryAccountIdentifier;
    }


    /**
     * Gets the acquiringRegistryCode value for this Transaction.
     * 
     * @return acquiringRegistryCode
     */
    public java.lang.String getAcquiringRegistryCode() {
        return acquiringRegistryCode;
    }


    /**
     * Sets the acquiringRegistryCode value for this Transaction.
     * 
     * @param acquiringRegistryCode
     */
    public void setAcquiringRegistryCode(java.lang.String acquiringRegistryCode) {
        this.acquiringRegistryCode = acquiringRegistryCode;
    }


    /**
     * Gets the acquiringRegistryAccountType value for this Transaction.
     * 
     * @return acquiringRegistryAccountType
     */
    public java.lang.Integer getAcquiringRegistryAccountType() {
        return acquiringRegistryAccountType;
    }


    /**
     * Sets the acquiringRegistryAccountType value for this Transaction.
     * 
     * @param acquiringRegistryAccountType
     */
    public void setAcquiringRegistryAccountType(java.lang.Integer acquiringRegistryAccountType) {
        this.acquiringRegistryAccountType = acquiringRegistryAccountType;
    }


    /**
     * Gets the acquiringRegistryAccountIdentifier value for this Transaction.
     * 
     * @return acquiringRegistryAccountIdentifier
     */
    public java.lang.Long getAcquiringRegistryAccountIdentifier() {
        return acquiringRegistryAccountIdentifier;
    }


    /**
     * Sets the acquiringRegistryAccountIdentifier value for this Transaction.
     * 
     * @param acquiringRegistryAccountIdentifier
     */
    public void setAcquiringRegistryAccountIdentifier(java.lang.Long acquiringRegistryAccountIdentifier) {
        this.acquiringRegistryAccountIdentifier = acquiringRegistryAccountIdentifier;
    }


    /**
     * Gets the notificationIdentifier value for this Transaction.
     * 
     * @return notificationIdentifier
     */
    public java.lang.Long getNotificationIdentifier() {
        return notificationIdentifier;
    }


    /**
     * Sets the notificationIdentifier value for this Transaction.
     * 
     * @param notificationIdentifier
     */
    public void setNotificationIdentifier(java.lang.Long notificationIdentifier) {
        this.notificationIdentifier = notificationIdentifier;
    }


    /**
     * Gets the transactionBlocks value for this Transaction.
     * 
     * @return transactionBlocks
     */
    public uk.gov.ets.lib.commons.kyoto.types.ReconciliationTransactionUnitBlock[] getTransactionBlocks() {
        return transactionBlocks;
    }


    /**
     * Sets the transactionBlocks value for this Transaction.
     * 
     * @param transactionBlocks
     */
    public void setTransactionBlocks(uk.gov.ets.lib.commons.kyoto.types.ReconciliationTransactionUnitBlock[] transactionBlocks) {
        this.transactionBlocks = transactionBlocks;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Transaction)) return false;
        Transaction other = (Transaction) obj;
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
            ((this.transactionStatusDateTime==null && other.getTransactionStatusDateTime()==null) || 
             (this.transactionStatusDateTime!=null &&
              this.transactionStatusDateTime.equals(other.getTransactionStatusDateTime()))) &&
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
            ((this.transactionBlocks==null && other.getTransactionBlocks()==null) || 
             (this.transactionBlocks!=null &&
              java.util.Arrays.equals(this.transactionBlocks, other.getTransactionBlocks())));
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
        if (getTransactionStatusDateTime() != null) {
            _hashCode += getTransactionStatusDateTime().hashCode();
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
        if (getTransactionBlocks() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getTransactionBlocks());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getTransactionBlocks(), i);
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
