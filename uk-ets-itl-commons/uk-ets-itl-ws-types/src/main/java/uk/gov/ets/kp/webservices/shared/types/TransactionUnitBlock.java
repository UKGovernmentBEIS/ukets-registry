/**
 * TransactionUnitBlock.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.gov.ets.kp.webservices.shared.types;

public class TransactionUnitBlock  implements java.io.Serializable {
    private long unitSerialBlockStart;

    private long unitSerialBlockEnd;

    private java.lang.String originatingRegistryCode;

    private int unitType;

    private java.lang.Integer suppUnitType;

    private int originalCommitPeriod;

    private int applicableCommitPeriod;

    private java.lang.Integer LULUCFActivity;

    private java.lang.Integer projectIdentifier;

    private java.lang.Integer track;

    private java.lang.String blockRole;

    private java.lang.Integer transferringRegistryAccountType;

    private java.lang.Long transferringRegistryAccountIdentifier;

    private java.lang.Integer acquiringRegistryAccountType;

    private java.lang.Long acquiringRegistryAccountIdentifier;

    private java.lang.Integer yearInCommitmentPeriod;

    private java.lang.Long installationIdentifier;

    private java.util.Date expiryDate;

    public TransactionUnitBlock() {
    }

    public TransactionUnitBlock(
           long unitSerialBlockStart,
           long unitSerialBlockEnd,
           java.lang.String originatingRegistryCode,
           int unitType,
           java.lang.Integer suppUnitType,
           int originalCommitPeriod,
           int applicableCommitPeriod,
           java.lang.Integer LULUCFActivity,
           java.lang.Integer projectIdentifier,
           java.lang.Integer track,
           java.lang.String blockRole,
           java.lang.Integer transferringRegistryAccountType,
           java.lang.Long transferringRegistryAccountIdentifier,
           java.lang.Integer acquiringRegistryAccountType,
           java.lang.Long acquiringRegistryAccountIdentifier,
           java.lang.Integer yearInCommitmentPeriod,
           java.lang.Long installationIdentifier,
           java.util.Date expiryDate) {
           this.unitSerialBlockStart = unitSerialBlockStart;
           this.unitSerialBlockEnd = unitSerialBlockEnd;
           this.originatingRegistryCode = originatingRegistryCode;
           this.unitType = unitType;
           this.suppUnitType = suppUnitType;
           this.originalCommitPeriod = originalCommitPeriod;
           this.applicableCommitPeriod = applicableCommitPeriod;
           this.LULUCFActivity = LULUCFActivity;
           this.projectIdentifier = projectIdentifier;
           this.track = track;
           this.blockRole = blockRole;
           this.transferringRegistryAccountType = transferringRegistryAccountType;
           this.transferringRegistryAccountIdentifier = transferringRegistryAccountIdentifier;
           this.acquiringRegistryAccountType = acquiringRegistryAccountType;
           this.acquiringRegistryAccountIdentifier = acquiringRegistryAccountIdentifier;
           this.yearInCommitmentPeriod = yearInCommitmentPeriod;
           this.installationIdentifier = installationIdentifier;
           this.expiryDate = expiryDate;
    }


    /**
     * Gets the unitSerialBlockStart value for this TransactionUnitBlock.
     * 
     * @return unitSerialBlockStart
     */
    public long getUnitSerialBlockStart() {
        return unitSerialBlockStart;
    }


    /**
     * Sets the unitSerialBlockStart value for this TransactionUnitBlock.
     * 
     * @param unitSerialBlockStart
     */
    public void setUnitSerialBlockStart(long unitSerialBlockStart) {
        this.unitSerialBlockStart = unitSerialBlockStart;
    }


    /**
     * Gets the unitSerialBlockEnd value for this TransactionUnitBlock.
     * 
     * @return unitSerialBlockEnd
     */
    public long getUnitSerialBlockEnd() {
        return unitSerialBlockEnd;
    }


    /**
     * Sets the unitSerialBlockEnd value for this TransactionUnitBlock.
     * 
     * @param unitSerialBlockEnd
     */
    public void setUnitSerialBlockEnd(long unitSerialBlockEnd) {
        this.unitSerialBlockEnd = unitSerialBlockEnd;
    }


    /**
     * Gets the originatingRegistryCode value for this TransactionUnitBlock.
     * 
     * @return originatingRegistryCode
     */
    public java.lang.String getOriginatingRegistryCode() {
        return originatingRegistryCode;
    }


    /**
     * Sets the originatingRegistryCode value for this TransactionUnitBlock.
     * 
     * @param originatingRegistryCode
     */
    public void setOriginatingRegistryCode(java.lang.String originatingRegistryCode) {
        this.originatingRegistryCode = originatingRegistryCode;
    }


    /**
     * Gets the unitType value for this TransactionUnitBlock.
     * 
     * @return unitType
     */
    public int getUnitType() {
        return unitType;
    }


    /**
     * Sets the unitType value for this TransactionUnitBlock.
     * 
     * @param unitType
     */
    public void setUnitType(int unitType) {
        this.unitType = unitType;
    }


    /**
     * Gets the suppUnitType value for this TransactionUnitBlock.
     * 
     * @return suppUnitType
     */
    public java.lang.Integer getSuppUnitType() {
        return suppUnitType;
    }


    /**
     * Sets the suppUnitType value for this TransactionUnitBlock.
     * 
     * @param suppUnitType
     */
    public void setSuppUnitType(java.lang.Integer suppUnitType) {
        this.suppUnitType = suppUnitType;
    }


    /**
     * Gets the originalCommitPeriod value for this TransactionUnitBlock.
     * 
     * @return originalCommitPeriod
     */
    public int getOriginalCommitPeriod() {
        return originalCommitPeriod;
    }


    /**
     * Sets the originalCommitPeriod value for this TransactionUnitBlock.
     * 
     * @param originalCommitPeriod
     */
    public void setOriginalCommitPeriod(int originalCommitPeriod) {
        this.originalCommitPeriod = originalCommitPeriod;
    }


    /**
     * Gets the applicableCommitPeriod value for this TransactionUnitBlock.
     * 
     * @return applicableCommitPeriod
     */
    public int getApplicableCommitPeriod() {
        return applicableCommitPeriod;
    }


    /**
     * Sets the applicableCommitPeriod value for this TransactionUnitBlock.
     * 
     * @param applicableCommitPeriod
     */
    public void setApplicableCommitPeriod(int applicableCommitPeriod) {
        this.applicableCommitPeriod = applicableCommitPeriod;
    }


    /**
     * Gets the LULUCFActivity value for this TransactionUnitBlock.
     * 
     * @return LULUCFActivity
     */
    public java.lang.Integer getLULUCFActivity() {
        return LULUCFActivity;
    }


    /**
     * Sets the LULUCFActivity value for this TransactionUnitBlock.
     * 
     * @param LULUCFActivity
     */
    public void setLULUCFActivity(java.lang.Integer LULUCFActivity) {
        this.LULUCFActivity = LULUCFActivity;
    }


    /**
     * Gets the projectIdentifier value for this TransactionUnitBlock.
     * 
     * @return projectIdentifier
     */
    public java.lang.Integer getProjectIdentifier() {
        return projectIdentifier;
    }


    /**
     * Sets the projectIdentifier value for this TransactionUnitBlock.
     * 
     * @param projectIdentifier
     */
    public void setProjectIdentifier(java.lang.Integer projectIdentifier) {
        this.projectIdentifier = projectIdentifier;
    }


    /**
     * Gets the track value for this TransactionUnitBlock.
     * 
     * @return track
     */
    public java.lang.Integer getTrack() {
        return track;
    }


    /**
     * Sets the track value for this TransactionUnitBlock.
     * 
     * @param track
     */
    public void setTrack(java.lang.Integer track) {
        this.track = track;
    }


    /**
     * Gets the blockRole value for this TransactionUnitBlock.
     * 
     * @return blockRole
     */
    public java.lang.String getBlockRole() {
        return blockRole;
    }


    /**
     * Sets the blockRole value for this TransactionUnitBlock.
     * 
     * @param blockRole
     */
    public void setBlockRole(java.lang.String blockRole) {
        this.blockRole = blockRole;
    }


    /**
     * Gets the transferringRegistryAccountType value for this TransactionUnitBlock.
     * 
     * @return transferringRegistryAccountType
     */
    public java.lang.Integer getTransferringRegistryAccountType() {
        return transferringRegistryAccountType;
    }


    /**
     * Sets the transferringRegistryAccountType value for this TransactionUnitBlock.
     * 
     * @param transferringRegistryAccountType
     */
    public void setTransferringRegistryAccountType(java.lang.Integer transferringRegistryAccountType) {
        this.transferringRegistryAccountType = transferringRegistryAccountType;
    }


    /**
     * Gets the transferringRegistryAccountIdentifier value for this TransactionUnitBlock.
     * 
     * @return transferringRegistryAccountIdentifier
     */
    public java.lang.Long getTransferringRegistryAccountIdentifier() {
        return transferringRegistryAccountIdentifier;
    }


    /**
     * Sets the transferringRegistryAccountIdentifier value for this TransactionUnitBlock.
     * 
     * @param transferringRegistryAccountIdentifier
     */
    public void setTransferringRegistryAccountIdentifier(java.lang.Long transferringRegistryAccountIdentifier) {
        this.transferringRegistryAccountIdentifier = transferringRegistryAccountIdentifier;
    }


    /**
     * Gets the acquiringRegistryAccountType value for this TransactionUnitBlock.
     * 
     * @return acquiringRegistryAccountType
     */
    public java.lang.Integer getAcquiringRegistryAccountType() {
        return acquiringRegistryAccountType;
    }


    /**
     * Sets the acquiringRegistryAccountType value for this TransactionUnitBlock.
     * 
     * @param acquiringRegistryAccountType
     */
    public void setAcquiringRegistryAccountType(java.lang.Integer acquiringRegistryAccountType) {
        this.acquiringRegistryAccountType = acquiringRegistryAccountType;
    }


    /**
     * Gets the acquiringRegistryAccountIdentifier value for this TransactionUnitBlock.
     * 
     * @return acquiringRegistryAccountIdentifier
     */
    public java.lang.Long getAcquiringRegistryAccountIdentifier() {
        return acquiringRegistryAccountIdentifier;
    }


    /**
     * Sets the acquiringRegistryAccountIdentifier value for this TransactionUnitBlock.
     * 
     * @param acquiringRegistryAccountIdentifier
     */
    public void setAcquiringRegistryAccountIdentifier(java.lang.Long acquiringRegistryAccountIdentifier) {
        this.acquiringRegistryAccountIdentifier = acquiringRegistryAccountIdentifier;
    }


    /**
     * Gets the yearInCommitmentPeriod value for this TransactionUnitBlock.
     * 
     * @return yearInCommitmentPeriod
     */
    public java.lang.Integer getYearInCommitmentPeriod() {
        return yearInCommitmentPeriod;
    }


    /**
     * Sets the yearInCommitmentPeriod value for this TransactionUnitBlock.
     * 
     * @param yearInCommitmentPeriod
     */
    public void setYearInCommitmentPeriod(java.lang.Integer yearInCommitmentPeriod) {
        this.yearInCommitmentPeriod = yearInCommitmentPeriod;
    }


    /**
     * Gets the installationIdentifier value for this TransactionUnitBlock.
     * 
     * @return installationIdentifier
     */
    public java.lang.Long getInstallationIdentifier() {
        return installationIdentifier;
    }


    /**
     * Sets the installationIdentifier value for this TransactionUnitBlock.
     * 
     * @param installationIdentifier
     */
    public void setInstallationIdentifier(java.lang.Long installationIdentifier) {
        this.installationIdentifier = installationIdentifier;
    }


    /**
     * Gets the expiryDate value for this TransactionUnitBlock.
     * 
     * @return expiryDate
     */
    public java.util.Date getExpiryDate() {
        return expiryDate;
    }


    /**
     * Sets the expiryDate value for this TransactionUnitBlock.
     * 
     * @param expiryDate
     */
    public void setExpiryDate(java.util.Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TransactionUnitBlock)) return false;
        TransactionUnitBlock other = (TransactionUnitBlock) obj;
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
              this.originatingRegistryCode.equals(other.getOriginatingRegistryCode()))) &&
            this.unitType == other.getUnitType() &&
            ((this.suppUnitType==null && other.getSuppUnitType()==null) || 
             (this.suppUnitType!=null &&
              this.suppUnitType.equals(other.getSuppUnitType()))) &&
            this.originalCommitPeriod == other.getOriginalCommitPeriod() &&
            this.applicableCommitPeriod == other.getApplicableCommitPeriod() &&
            ((this.LULUCFActivity==null && other.getLULUCFActivity()==null) || 
             (this.LULUCFActivity!=null &&
              this.LULUCFActivity.equals(other.getLULUCFActivity()))) &&
            ((this.projectIdentifier==null && other.getProjectIdentifier()==null) || 
             (this.projectIdentifier!=null &&
              this.projectIdentifier.equals(other.getProjectIdentifier()))) &&
            ((this.track==null && other.getTrack()==null) || 
             (this.track!=null &&
              this.track.equals(other.getTrack()))) &&
            ((this.blockRole==null && other.getBlockRole()==null) || 
             (this.blockRole!=null &&
              this.blockRole.equals(other.getBlockRole()))) &&
            ((this.transferringRegistryAccountType==null && other.getTransferringRegistryAccountType()==null) || 
             (this.transferringRegistryAccountType!=null &&
              this.transferringRegistryAccountType.equals(other.getTransferringRegistryAccountType()))) &&
            ((this.transferringRegistryAccountIdentifier==null && other.getTransferringRegistryAccountIdentifier()==null) || 
             (this.transferringRegistryAccountIdentifier!=null &&
              this.transferringRegistryAccountIdentifier.equals(other.getTransferringRegistryAccountIdentifier()))) &&
            ((this.acquiringRegistryAccountType==null && other.getAcquiringRegistryAccountType()==null) || 
             (this.acquiringRegistryAccountType!=null &&
              this.acquiringRegistryAccountType.equals(other.getAcquiringRegistryAccountType()))) &&
            ((this.acquiringRegistryAccountIdentifier==null && other.getAcquiringRegistryAccountIdentifier()==null) || 
             (this.acquiringRegistryAccountIdentifier!=null &&
              this.acquiringRegistryAccountIdentifier.equals(other.getAcquiringRegistryAccountIdentifier()))) &&
            ((this.yearInCommitmentPeriod==null && other.getYearInCommitmentPeriod()==null) || 
             (this.yearInCommitmentPeriod!=null &&
              this.yearInCommitmentPeriod.equals(other.getYearInCommitmentPeriod()))) &&
            ((this.installationIdentifier==null && other.getInstallationIdentifier()==null) || 
             (this.installationIdentifier!=null &&
              this.installationIdentifier.equals(other.getInstallationIdentifier()))) &&
            ((this.expiryDate==null && other.getExpiryDate()==null) || 
             (this.expiryDate!=null &&
              this.expiryDate.equals(other.getExpiryDate())));
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
        _hashCode += getUnitType();
        if (getSuppUnitType() != null) {
            _hashCode += getSuppUnitType().hashCode();
        }
        _hashCode += getOriginalCommitPeriod();
        _hashCode += getApplicableCommitPeriod();
        if (getLULUCFActivity() != null) {
            _hashCode += getLULUCFActivity().hashCode();
        }
        if (getProjectIdentifier() != null) {
            _hashCode += getProjectIdentifier().hashCode();
        }
        if (getTrack() != null) {
            _hashCode += getTrack().hashCode();
        }
        if (getBlockRole() != null) {
            _hashCode += getBlockRole().hashCode();
        }
        if (getTransferringRegistryAccountType() != null) {
            _hashCode += getTransferringRegistryAccountType().hashCode();
        }
        if (getTransferringRegistryAccountIdentifier() != null) {
            _hashCode += getTransferringRegistryAccountIdentifier().hashCode();
        }
        if (getAcquiringRegistryAccountType() != null) {
            _hashCode += getAcquiringRegistryAccountType().hashCode();
        }
        if (getAcquiringRegistryAccountIdentifier() != null) {
            _hashCode += getAcquiringRegistryAccountIdentifier().hashCode();
        }
        if (getYearInCommitmentPeriod() != null) {
            _hashCode += getYearInCommitmentPeriod().hashCode();
        }
        if (getInstallationIdentifier() != null) {
            _hashCode += getInstallationIdentifier().hashCode();
        }
        if (getExpiryDate() != null) {
            _hashCode += getExpiryDate().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TransactionUnitBlock.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "TransactionUnitBlock"));
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unitType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "unitType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("suppUnitType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "suppUnitType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("originalCommitPeriod");
        elemField.setXmlName(new javax.xml.namespace.QName("", "originalCommitPeriod"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("applicableCommitPeriod");
        elemField.setXmlName(new javax.xml.namespace.QName("", "applicableCommitPeriod"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("LULUCFActivity");
        elemField.setXmlName(new javax.xml.namespace.QName("", "LULUCFActivity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("projectIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "projectIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("track");
        elemField.setXmlName(new javax.xml.namespace.QName("", "track"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("blockRole");
        elemField.setXmlName(new javax.xml.namespace.QName("", "blockRole"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
        elemField.setFieldName("yearInCommitmentPeriod");
        elemField.setXmlName(new javax.xml.namespace.QName("", "yearInCommitmentPeriod"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("installationIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "installationIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("expiryDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "expiryDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
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
        return "TransactionUnitBlock{" +
                "unitSerialBlockStart=" + unitSerialBlockStart +
                ", unitSerialBlockEnd=" + unitSerialBlockEnd +
                ", originatingRegistryCode='" + originatingRegistryCode + '\'' +
                ", unitType=" + unitType +
                ", suppUnitType=" + suppUnitType +
                ", originalCommitPeriod=" + originalCommitPeriod +
                ", applicableCommitPeriod=" + applicableCommitPeriod +
                ", LULUCFActivity=" + LULUCFActivity +
                ", projectIdentifier=" + projectIdentifier +
                ", track=" + track +
                ", blockRole='" + blockRole + '\'' +
                ", transferringRegistryAccountType=" + transferringRegistryAccountType +
                ", transferringRegistryAccountIdentifier=" + transferringRegistryAccountIdentifier +
                ", acquiringRegistryAccountType=" + acquiringRegistryAccountType +
                ", acquiringRegistryAccountIdentifier=" + acquiringRegistryAccountIdentifier +
                ", yearInCommitmentPeriod=" + yearInCommitmentPeriod +
                ", installationIdentifier=" + installationIdentifier +
                ", expiryDate=" + expiryDate +
                '}';
    }
}
