/**
 * ProvideTimeResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.gov.ets.lib.commons.kyoto.types;

public class ProvideTimeResponse  implements java.io.Serializable {
    private java.util.Calendar systemTime;

    private int resultIdentifier;

    private int[] responseCodes;

    public ProvideTimeResponse() {
    }

    public ProvideTimeResponse(
           java.util.Calendar systemTime,
           int resultIdentifier,
           int[] responseCodes) {
           this.systemTime = systemTime;
           this.resultIdentifier = resultIdentifier;
           this.responseCodes = responseCodes;
    }


    /**
     * Gets the systemTime value for this ProvideTimeResponse.
     * 
     * @return systemTime
     */
    public java.util.Calendar getSystemTime() {
        return systemTime;
    }


    /**
     * Sets the systemTime value for this ProvideTimeResponse.
     * 
     * @param systemTime
     */
    public void setSystemTime(java.util.Calendar systemTime) {
        this.systemTime = systemTime;
    }


    /**
     * Gets the resultIdentifier value for this ProvideTimeResponse.
     * 
     * @return resultIdentifier
     */
    public int getResultIdentifier() {
        return resultIdentifier;
    }


    /**
     * Sets the resultIdentifier value for this ProvideTimeResponse.
     * 
     * @param resultIdentifier
     */
    public void setResultIdentifier(int resultIdentifier) {
        this.resultIdentifier = resultIdentifier;
    }


    /**
     * Gets the responseCodes value for this ProvideTimeResponse.
     * 
     * @return responseCodes
     */
    public int[] getResponseCodes() {
        return responseCodes;
    }


    /**
     * Sets the responseCodes value for this ProvideTimeResponse.
     * 
     * @param responseCodes
     */
    public void setResponseCodes(int[] responseCodes) {
        this.responseCodes = responseCodes;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ProvideTimeResponse)) return false;
        ProvideTimeResponse other = (ProvideTimeResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.systemTime==null && other.getSystemTime()==null) || 
             (this.systemTime!=null &&
              this.systemTime.equals(other.getSystemTime()))) &&
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
        if (getSystemTime() != null) {
            _hashCode += getSystemTime().hashCode();
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
}
