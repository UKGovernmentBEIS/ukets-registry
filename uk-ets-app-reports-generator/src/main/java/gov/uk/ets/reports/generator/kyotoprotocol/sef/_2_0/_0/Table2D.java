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
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}AdaptationFundType" maxOccurs="3" minOccurs="3"/>
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
        "adaptationFundType"
})
@XmlRootElement(name = "Table2d")
public class Table2D {

    @XmlElement(name = "AdaptationFundType", required = true)
    protected List<AdaptationFundType> adaptationFundType;

    /**
     * Gets the value of the adaptationFundType property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the adaptationFundType property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdaptationFundType().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AdaptationFundType }
     *
     *
     */
    public List<AdaptationFundType> getAdaptationFundType() {
        if (adaptationFundType == null) {
            adaptationFundType = new ArrayList<AdaptationFundType>();
        }
        return this.adaptationFundType;
    }

}
