package gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0;

import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.CorrectiveTransaction;

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
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}CorrectiveTransaction" maxOccurs="unbounded" minOccurs="0"/>
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
        "correctiveTransaction"
})
@XmlRootElement(name = "Table6a")
public class Table6A {

    @XmlElement(name = "CorrectiveTransaction", namespace = "urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0")
    protected List<CorrectiveTransaction> correctiveTransaction;

    /**
     * Gets the value of the correctiveTransaction property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the correctiveTransaction property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCorrectiveTransaction().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CorrectiveTransaction }
     *
     *
     */
    public List<CorrectiveTransaction> getCorrectiveTransaction() {
        if (correctiveTransaction == null) {
            correctiveTransaction = new ArrayList<CorrectiveTransaction>();
        }
        return this.correctiveTransaction;
    }

}
