package gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0;

import jakarta.xml.bind.annotation.*;
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
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}AnnualRetirement" maxOccurs="8" minOccurs="8"/>
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
        "annualRetirement"
})
@XmlRootElement(name = "AnnualRetirements")
public class AnnualRetirements {

    @XmlElement(name = "AnnualRetirement", required = true)
    protected List<AnnualRetirement> annualRetirement;

    /**
     * Gets the value of the annualRetirement property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the annualRetirement property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnnualRetirement().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AnnualRetirement }
     *
     *
     */
    public List<AnnualRetirement> getAnnualRetirement() {
        if (annualRetirement == null) {
            annualRetirement = new ArrayList<AnnualRetirement>();
        }
        return this.annualRetirement;
    }

}
