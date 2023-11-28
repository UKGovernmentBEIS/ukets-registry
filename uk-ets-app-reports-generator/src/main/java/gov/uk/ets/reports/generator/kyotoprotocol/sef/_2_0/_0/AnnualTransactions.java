package gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

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
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}AnnualTransaction" maxOccurs="11" minOccurs="8"/>
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
        "annualTransaction"
})
@XmlRootElement(name = "AnnualTransactions")
public class AnnualTransactions {

    @XmlElement(name = "AnnualTransaction", required = true)
    protected List<AnnualTransaction> annualTransaction;

    /**
     * Gets the value of the annualTransaction property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the annualTransaction property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnnualTransaction().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AnnualTransaction }
     *
     *
     */
    public List<AnnualTransaction> getAnnualTransaction() {
        if (annualTransaction == null) {
            annualTransaction = new ArrayList<AnnualTransaction>();
        }
        return this.annualTransaction;
    }

}
