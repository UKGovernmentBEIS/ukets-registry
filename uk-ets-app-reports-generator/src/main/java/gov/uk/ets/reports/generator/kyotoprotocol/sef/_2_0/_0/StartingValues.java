package gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0;

import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.StartingValue;

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
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}StartingValue" maxOccurs="7" minOccurs="7"/>
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
        "startingValue"
})
@XmlRootElement(name = "StartingValues")
public class StartingValues {

    @XmlElement(name = "StartingValue", namespace = "urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0", required = true)
    protected List<StartingValue> startingValue;

    /**
     * Gets the value of the startingValue property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the startingValue property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStartingValue().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StartingValue }
     *
     *
     */
    public List<StartingValue> getStartingValue() {
        if (startingValue == null) {
            startingValue = new ArrayList<StartingValue>();
        }
        return this.startingValue;
    }

}
