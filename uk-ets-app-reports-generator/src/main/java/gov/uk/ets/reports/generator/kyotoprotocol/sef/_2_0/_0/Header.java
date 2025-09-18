package gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0;

import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SourceType;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.StatusType;

import jakarta.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}Party"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}SubmissionYear"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}ReportedYear"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}CommitmentPeriod"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}Version"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}Source"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}Status"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}Timestamp"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}ReportFormat"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}ReportEndDate" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "party",
        "submissionYear",
        "reportedYear",
        "commitmentPeriod",
        "version",
        "source",
        "status",
        "timestamp",
        "reportFormat",
        "reportEndDate"
})
@XmlRootElement(name = "Header")
public class Header {

    @XmlElement(name = "Party", namespace = "urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0", required = true)
    protected String party;
    @XmlElement(name = "SubmissionYear", namespace = "urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0")
    protected short submissionYear;
    @XmlElement(name = "ReportedYear", namespace = "urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0")
    protected short reportedYear;
    @XmlElement(name = "CommitmentPeriod", namespace = "urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0")
    protected byte commitmentPeriod;
    @XmlElement(name = "Version", namespace = "urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0")
    protected short version;
    @XmlElement(name = "Source", namespace = "urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0", required = true)
    protected SourceType source;
    @XmlElement(name = "Status", namespace = "urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0", required = true)
    protected StatusType status;
    @XmlElement(name = "Timestamp", namespace = "urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timestamp;
    @XmlElement(name = "ReportFormat", required = true)
    protected String reportFormat;
    @XmlElement(name = "ReportEndDate", namespace = "urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0")
    protected String reportEndDate;

    /**
     * Gets the value of the party property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getParty() {
        return party;
    }

    /**
     * Sets the value of the party property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setParty(String value) {
        this.party = value;
    }

    /**
     * Gets the value of the submissionYear property.
     *
     */
    public short getSubmissionYear() {
        return submissionYear;
    }

    /**
     * Sets the value of the submissionYear property.
     *
     */
    public void setSubmissionYear(short value) {
        this.submissionYear = value;
    }

    /**
     * Gets the value of the reportedYear property.
     *
     */
    public short getReportedYear() {
        return reportedYear;
    }

    /**
     * Sets the value of the reportedYear property.
     *
     */
    public void setReportedYear(short value) {
        this.reportedYear = value;
    }

    /**
     * Gets the value of the commitmentPeriod property.
     *
     */
    public byte getCommitmentPeriod() {
        return commitmentPeriod;
    }

    /**
     * Sets the value of the commitmentPeriod property.
     *
     */
    public void setCommitmentPeriod(byte value) {
        this.commitmentPeriod = value;
    }

    /**
     * Gets the value of the version property.
     *
     */
    public short getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     *
     */
    public void setVersion(short value) {
        this.version = value;
    }

    /**
     * Gets the value of the source property.
     *
     * @return
     *     possible object is
     *     {@link SourceType }
     *
     */
    public SourceType getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     *
     * @param value
     *     allowed object is
     *     {@link SourceType }
     *
     */
    public void setSource(SourceType value) {
        this.source = value;
    }

    /**
     * Gets the value of the status property.
     *
     * @return
     *     possible object is
     *     {@link StatusType }
     *
     */
    public StatusType getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     *
     * @param value
     *     allowed object is
     *     {@link StatusType }
     *
     */
    public void setStatus(StatusType value) {
        this.status = value;
    }

    /**
     * Gets the value of the timestamp property.
     *
     * @return
     *     possible object is
     *     {@link javax.xml.datatype.XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     *
     * @param value
     *     allowed object is
     *     {@link javax.xml.datatype.XMLGregorianCalendar }
     *
     */
    public void setTimestamp(XMLGregorianCalendar value) {
        this.timestamp = value;
    }

    /**
     * Gets the value of the reportFormat property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getReportFormat() {
        return reportFormat;
    }

    /**
     * Sets the value of the reportFormat property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setReportFormat(String value) {
        this.reportFormat = value;
    }

    /**
     * Gets the value of the reportEndDate property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getReportEndDate() {
        return reportEndDate;
    }

    /**
     * Sets the value of the reportEndDate property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setReportEndDate(String value) {
        this.reportEndDate = value;
    }

}
