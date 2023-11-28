/**
 * ReceiveUnitBlocksRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.gov.ets.lib.commons.kyoto.types;

public class ReceiveUnitBlocksRequest  implements java.io.Serializable {
    private java.lang.String from;

    private java.lang.String to;

    private int majorVersion;

    private int minorVersion;

    private java.lang.String reconciliationIdentifier;

    private uk.gov.ets.lib.commons.kyoto.types.UnitBlock[] unitBlocks;

    public ReceiveUnitBlocksRequest() {
    }

    public ReceiveUnitBlocksRequest(
           java.lang.String from,
           java.lang.String to,
           int majorVersion,
           int minorVersion,
           java.lang.String reconciliationIdentifier,
           uk.gov.ets.lib.commons.kyoto.types.UnitBlock[] unitBlocks) {
           this.from = from;
           this.to = to;
           this.majorVersion = majorVersion;
           this.minorVersion = minorVersion;
           this.reconciliationIdentifier = reconciliationIdentifier;
           this.unitBlocks = unitBlocks;
    }


    /**
     * Gets the from value for this ReceiveUnitBlocksRequest.
     * 
     * @return from
     */
    public java.lang.String getFrom() {
        return from;
    }


    /**
     * Sets the from value for this ReceiveUnitBlocksRequest.
     * 
     * @param from
     */
    public void setFrom(java.lang.String from) {
        this.from = from;
    }


    /**
     * Gets the to value for this ReceiveUnitBlocksRequest.
     * 
     * @return to
     */
    public java.lang.String getTo() {
        return to;
    }


    /**
     * Sets the to value for this ReceiveUnitBlocksRequest.
     * 
     * @param to
     */
    public void setTo(java.lang.String to) {
        this.to = to;
    }


    /**
     * Gets the majorVersion value for this ReceiveUnitBlocksRequest.
     * 
     * @return majorVersion
     */
    public int getMajorVersion() {
        return majorVersion;
    }


    /**
     * Sets the majorVersion value for this ReceiveUnitBlocksRequest.
     * 
     * @param majorVersion
     */
    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }


    /**
     * Gets the minorVersion value for this ReceiveUnitBlocksRequest.
     * 
     * @return minorVersion
     */
    public int getMinorVersion() {
        return minorVersion;
    }


    /**
     * Sets the minorVersion value for this ReceiveUnitBlocksRequest.
     * 
     * @param minorVersion
     */
    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }


    /**
     * Gets the reconciliationIdentifier value for this ReceiveUnitBlocksRequest.
     * 
     * @return reconciliationIdentifier
     */
    public java.lang.String getReconciliationIdentifier() {
        return reconciliationIdentifier;
    }


    /**
     * Sets the reconciliationIdentifier value for this ReceiveUnitBlocksRequest.
     * 
     * @param reconciliationIdentifier
     */
    public void setReconciliationIdentifier(java.lang.String reconciliationIdentifier) {
        this.reconciliationIdentifier = reconciliationIdentifier;
    }


    /**
     * Gets the unitBlocks value for this ReceiveUnitBlocksRequest.
     * 
     * @return unitBlocks
     */
    public uk.gov.ets.lib.commons.kyoto.types.UnitBlock[] getUnitBlocks() {
        return unitBlocks;
    }


    /**
     * Sets the unitBlocks value for this ReceiveUnitBlocksRequest.
     * 
     * @param unitBlocks
     */
    public void setUnitBlocks(uk.gov.ets.lib.commons.kyoto.types.UnitBlock[] unitBlocks) {
        this.unitBlocks = unitBlocks;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ReceiveUnitBlocksRequest)) return false;
        ReceiveUnitBlocksRequest other = (ReceiveUnitBlocksRequest) obj;
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
            ((this.reconciliationIdentifier==null && other.getReconciliationIdentifier()==null) || 
             (this.reconciliationIdentifier!=null &&
              this.reconciliationIdentifier.equals(other.getReconciliationIdentifier()))) &&
            ((this.unitBlocks==null && other.getUnitBlocks()==null) || 
             (this.unitBlocks!=null &&
              java.util.Arrays.equals(this.unitBlocks, other.getUnitBlocks())));
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
        if (getReconciliationIdentifier() != null) {
            _hashCode += getReconciliationIdentifier().hashCode();
        }
        if (getUnitBlocks() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getUnitBlocks());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getUnitBlocks(), i);
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
