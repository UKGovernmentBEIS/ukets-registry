/**
 * ProvideUnitBlocksRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.gov.ets.lib.commons.kyoto.types;

public class ProvideUnitBlocksRequest  implements java.io.Serializable {
    private java.lang.String from;

    private java.lang.String to;

    private int majorVersion;

    private int minorVersion;

    private java.lang.String reconciliationIdentifier;

    private java.util.Calendar reconciliationSnapshotDatetime;

    private uk.gov.ets.lib.commons.kyoto.types.UnitBlockRequest[] unitBlockRequests;

    private int[] responseCodes;

    public ProvideUnitBlocksRequest() {
    }

    public ProvideUnitBlocksRequest(
           java.lang.String from,
           java.lang.String to,
           int majorVersion,
           int minorVersion,
           java.lang.String reconciliationIdentifier,
           java.util.Calendar reconciliationSnapshotDatetime,
           uk.gov.ets.lib.commons.kyoto.types.UnitBlockRequest[] unitBlockRequests,
           int[] responseCodes) {
           this.from = from;
           this.to = to;
           this.majorVersion = majorVersion;
           this.minorVersion = minorVersion;
           this.reconciliationIdentifier = reconciliationIdentifier;
           this.reconciliationSnapshotDatetime = reconciliationSnapshotDatetime;
           this.unitBlockRequests = unitBlockRequests;
           this.responseCodes = responseCodes;
    }


    /**
     * Gets the from value for this ProvideUnitBlocksRequest.
     * 
     * @return from
     */
    public java.lang.String getFrom() {
        return from;
    }


    /**
     * Sets the from value for this ProvideUnitBlocksRequest.
     * 
     * @param from
     */
    public void setFrom(java.lang.String from) {
        this.from = from;
    }


    /**
     * Gets the to value for this ProvideUnitBlocksRequest.
     * 
     * @return to
     */
    public java.lang.String getTo() {
        return to;
    }


    /**
     * Sets the to value for this ProvideUnitBlocksRequest.
     * 
     * @param to
     */
    public void setTo(java.lang.String to) {
        this.to = to;
    }


    /**
     * Gets the majorVersion value for this ProvideUnitBlocksRequest.
     * 
     * @return majorVersion
     */
    public int getMajorVersion() {
        return majorVersion;
    }


    /**
     * Sets the majorVersion value for this ProvideUnitBlocksRequest.
     * 
     * @param majorVersion
     */
    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }


    /**
     * Gets the minorVersion value for this ProvideUnitBlocksRequest.
     * 
     * @return minorVersion
     */
    public int getMinorVersion() {
        return minorVersion;
    }


    /**
     * Sets the minorVersion value for this ProvideUnitBlocksRequest.
     * 
     * @param minorVersion
     */
    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }


    /**
     * Gets the reconciliationIdentifier value for this ProvideUnitBlocksRequest.
     * 
     * @return reconciliationIdentifier
     */
    public java.lang.String getReconciliationIdentifier() {
        return reconciliationIdentifier;
    }


    /**
     * Sets the reconciliationIdentifier value for this ProvideUnitBlocksRequest.
     * 
     * @param reconciliationIdentifier
     */
    public void setReconciliationIdentifier(java.lang.String reconciliationIdentifier) {
        this.reconciliationIdentifier = reconciliationIdentifier;
    }


    /**
     * Gets the reconciliationSnapshotDatetime value for this ProvideUnitBlocksRequest.
     * 
     * @return reconciliationSnapshotDatetime
     */
    public java.util.Calendar getReconciliationSnapshotDatetime() {
        return reconciliationSnapshotDatetime;
    }


    /**
     * Sets the reconciliationSnapshotDatetime value for this ProvideUnitBlocksRequest.
     * 
     * @param reconciliationSnapshotDatetime
     */
    public void setReconciliationSnapshotDatetime(java.util.Calendar reconciliationSnapshotDatetime) {
        this.reconciliationSnapshotDatetime = reconciliationSnapshotDatetime;
    }


    /**
     * Gets the unitBlockRequests value for this ProvideUnitBlocksRequest.
     * 
     * @return unitBlockRequests
     */
    public uk.gov.ets.lib.commons.kyoto.types.UnitBlockRequest[] getUnitBlockRequests() {
        return unitBlockRequests;
    }


    /**
     * Sets the unitBlockRequests value for this ProvideUnitBlocksRequest.
     * 
     * @param unitBlockRequests
     */
    public void setUnitBlockRequests(uk.gov.ets.lib.commons.kyoto.types.UnitBlockRequest[] unitBlockRequests) {
        this.unitBlockRequests = unitBlockRequests;
    }


    /**
     * Gets the responseCodes value for this ProvideUnitBlocksRequest.
     * 
     * @return responseCodes
     */
    public int[] getResponseCodes() {
        return responseCodes;
    }


    /**
     * Sets the responseCodes value for this ProvideUnitBlocksRequest.
     * 
     * @param responseCodes
     */
    public void setResponseCodes(int[] responseCodes) {
        this.responseCodes = responseCodes;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ProvideUnitBlocksRequest)) return false;
        ProvideUnitBlocksRequest other = (ProvideUnitBlocksRequest) obj;
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
            ((this.reconciliationSnapshotDatetime==null && other.getReconciliationSnapshotDatetime()==null) || 
             (this.reconciliationSnapshotDatetime!=null &&
              this.reconciliationSnapshotDatetime.equals(other.getReconciliationSnapshotDatetime()))) &&
            ((this.unitBlockRequests==null && other.getUnitBlockRequests()==null) || 
             (this.unitBlockRequests!=null &&
              java.util.Arrays.equals(this.unitBlockRequests, other.getUnitBlockRequests()))) &&
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
        if (getReconciliationSnapshotDatetime() != null) {
            _hashCode += getReconciliationSnapshotDatetime().hashCode();
        }
        if (getUnitBlockRequests() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getUnitBlockRequests());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getUnitBlockRequests(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
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
}
