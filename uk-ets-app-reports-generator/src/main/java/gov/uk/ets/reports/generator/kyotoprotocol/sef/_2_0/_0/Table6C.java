package gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0;

import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.CorrectiveRetirement;

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
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}CorrectiveRetirement" maxOccurs="unbounded" minOccurs="0"/>
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
        "correctiveRetirement"
})
@XmlRootElement(name = "Table6c")
public class Table6C {

    @XmlElement(name = "CorrectiveRetirement", namespace = "urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0")
    protected List<CorrectiveRetirement> correctiveRetirement;

    /**
     * Gets the value of the correctiveRetirement property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the correctiveRetirement property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCorrectiveRetirement().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CorrectiveRetirement }
     *
     *
     */
    public List<CorrectiveRetirement> getCorrectiveRetirement() {
        if (correctiveRetirement == null) {
            correctiveRetirement = new ArrayList<CorrectiveRetirement>();
        }
        return this.correctiveRetirement;
    }

}
