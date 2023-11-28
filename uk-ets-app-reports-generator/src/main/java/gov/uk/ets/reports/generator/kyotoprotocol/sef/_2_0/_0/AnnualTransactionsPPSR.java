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
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}AnnualTransactionPPSR" maxOccurs="9" minOccurs="11"/>
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
        "annualTransactionPPSR"
})
@XmlRootElement(name = "AnnualTransactionsPPSR")
public class AnnualTransactionsPPSR {

    @XmlElement(name = "AnnualTransactionPPSR", required = true)
    protected List<AnnualTransactionPPSR> annualTransactionPPSR;

    /**
     * Gets the value of the annualTransactionPPSR property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the annualTransactionPPSR property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnnualTransactionPPSR().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AnnualTransactionPPSR }
     *
     *
     */
    public List<AnnualTransactionPPSR> getAnnualTransactionPPSR() {
        if (annualTransactionPPSR == null) {
            annualTransactionPPSR = new ArrayList<AnnualTransactionPPSR>();
        }
        return this.annualTransactionPPSR;
    }

}
