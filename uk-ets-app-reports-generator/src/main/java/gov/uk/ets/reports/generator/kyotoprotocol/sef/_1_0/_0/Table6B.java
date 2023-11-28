package gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0;

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
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}CorrectiveReplacement" maxOccurs="unbounded" minOccurs="0"/>
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
        "correctiveReplacement"
})
@XmlRootElement(name = "Table6b")
public class Table6B {

    @XmlElement(name = "CorrectiveReplacement")
    protected List<CorrectiveReplacement> correctiveReplacement;

    /**
     * Gets the value of the correctiveReplacement property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the correctiveReplacement property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCorrectiveReplacement().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CorrectiveReplacement }
     *
     *
     */
    public List<CorrectiveReplacement> getCorrectiveReplacement() {
        if (correctiveReplacement == null) {
            correctiveReplacement = new ArrayList<CorrectiveReplacement>();
        }
        return this.correctiveReplacement;
    }

}
