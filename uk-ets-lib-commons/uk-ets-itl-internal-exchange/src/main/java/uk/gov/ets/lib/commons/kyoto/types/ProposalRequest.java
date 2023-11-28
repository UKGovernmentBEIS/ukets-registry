/**
 * ProposalRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.gov.ets.lib.commons.kyoto.types;

public class ProposalRequest  implements java.io.Serializable {
    private java.lang.String from;

    private java.lang.String to;

    private int majorVersion;

    private int minorVersion;

    private uk.gov.ets.lib.commons.kyoto.types.ProposalTransaction proposedTransaction;

    public ProposalRequest() {
    }

    public ProposalRequest(
           java.lang.String from,
           java.lang.String to,
           int majorVersion,
           int minorVersion,
           uk.gov.ets.lib.commons.kyoto.types.ProposalTransaction proposedTransaction) {
           this.from = from;
           this.to = to;
           this.majorVersion = majorVersion;
           this.minorVersion = minorVersion;
           this.proposedTransaction = proposedTransaction;
    }


    /**
     * Gets the from value for this ProposalRequest.
     * 
     * @return from
     */
    public java.lang.String getFrom() {
        return from;
    }


    /**
     * Sets the from value for this ProposalRequest.
     * 
     * @param from
     */
    public void setFrom(java.lang.String from) {
        this.from = from;
    }


    /**
     * Gets the to value for this ProposalRequest.
     * 
     * @return to
     */
    public java.lang.String getTo() {
        return to;
    }


    /**
     * Sets the to value for this ProposalRequest.
     * 
     * @param to
     */
    public void setTo(java.lang.String to) {
        this.to = to;
    }


    /**
     * Gets the majorVersion value for this ProposalRequest.
     * 
     * @return majorVersion
     */
    public int getMajorVersion() {
        return majorVersion;
    }


    /**
     * Sets the majorVersion value for this ProposalRequest.
     * 
     * @param majorVersion
     */
    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }


    /**
     * Gets the minorVersion value for this ProposalRequest.
     * 
     * @return minorVersion
     */
    public int getMinorVersion() {
        return minorVersion;
    }


    /**
     * Sets the minorVersion value for this ProposalRequest.
     * 
     * @param minorVersion
     */
    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }


    /**
     * Gets the proposedTransaction value for this ProposalRequest.
     * 
     * @return proposedTransaction
     */
    public uk.gov.ets.lib.commons.kyoto.types.ProposalTransaction getProposedTransaction() {
        return proposedTransaction;
    }


    /**
     * Sets the proposedTransaction value for this ProposalRequest.
     * 
     * @param proposedTransaction
     */
    public void setProposedTransaction(uk.gov.ets.lib.commons.kyoto.types.ProposalTransaction proposedTransaction) {
        this.proposedTransaction = proposedTransaction;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ProposalRequest)) return false;
        ProposalRequest other = (ProposalRequest) obj;
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
            ((this.proposedTransaction==null && other.getProposedTransaction()==null) || 
             (this.proposedTransaction!=null &&
              this.proposedTransaction.equals(other.getProposedTransaction())));
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
        if (getProposedTransaction() != null) {
            _hashCode += getProposedTransaction().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }
}
