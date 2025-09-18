package gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0;

import jakarta.xml.bind.annotation.*;

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
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}StartingValues"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}AnnualTransactions"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}TotalAdditionSubtraction"/>
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
        "startingValues",
        "annualTransactions",
        "totalAdditionSubtraction"
})
@XmlRootElement(name = "Table5a")
public class Table5A {

    @XmlElement(name = "StartingValues", required = true)
    protected StartingValues startingValues;
    @XmlElement(name = "AnnualTransactions", required = true)
    protected AnnualTransactions annualTransactions;
    @XmlElement(name = "TotalAdditionSubtraction", required = true)
    protected TotalAdditionSubtraction totalAdditionSubtraction;

    /**
     * Gets the value of the startingValues property.
     *
     * @return
     *     possible object is
     *     {@link StartingValues }
     *
     */
    public StartingValues getStartingValues() {
        return startingValues;
    }

    /**
     * Sets the value of the startingValues property.
     *
     * @param value
     *     allowed object is
     *     {@link StartingValues }
     *
     */
    public void setStartingValues(StartingValues value) {
        this.startingValues = value;
    }

    /**
     * Gets the value of the annualTransactions property.
     *
     * @return
     *     possible object is
     *     {@link AnnualTransactions }
     *
     */
    public AnnualTransactions getAnnualTransactions() {
        return annualTransactions;
    }

    /**
     * Sets the value of the annualTransactions property.
     *
     * @param value
     *     allowed object is
     *     {@link AnnualTransactions }
     *
     */
    public void setAnnualTransactions(AnnualTransactions value) {
        this.annualTransactions = value;
    }

    /**
     * Gets the value of the totalAdditionSubtraction property.
     *
     * @return
     *     possible object is
     *     {@link TotalAdditionSubtraction }
     *
     */
    public TotalAdditionSubtraction getTotalAdditionSubtraction() {
        return totalAdditionSubtraction;
    }

    /**
     * Sets the value of the totalAdditionSubtraction property.
     *
     * @param value
     *     allowed object is
     *     {@link TotalAdditionSubtraction }
     *
     */
    public void setTotalAdditionSubtraction(TotalAdditionSubtraction value) {
        this.totalAdditionSubtraction = value;
    }

}
