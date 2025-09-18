package gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0;

import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TotalAdditionSubtraction;

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
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}AnnualTransactions"/>
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
        "annualTransactions",
        "totalAdditionSubtraction"
})
@XmlRootElement(name = "Table5b")
public class Table5B {

    @XmlElement(name = "AnnualTransactions", required = true)
    protected AnnualTransactions annualTransactions;
    @XmlElement(name = "TotalAdditionSubtraction", namespace = "urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0", required = true)
    protected TotalAdditionSubtraction totalAdditionSubtraction;

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
