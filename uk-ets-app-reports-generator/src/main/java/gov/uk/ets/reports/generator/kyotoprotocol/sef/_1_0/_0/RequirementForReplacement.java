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
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}CerQty" maxOccurs="3" minOccurs="2"/>
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
        "cerQty"
})
@XmlRootElement(name = "RequirementForReplacement")
public class RequirementForReplacement {

    @XmlElement(name = "CerQty", required = true)
    protected List<CerQty> cerQty;

    /**
     * Gets the value of the cerQty property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cerQty property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCerQty().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CerQty }
     *
     *
     */
    public List<CerQty> getCerQty() {
        if (cerQty == null) {
            cerQty = new ArrayList<CerQty>();
        }
        return this.cerQty;
    }

}
