package gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0;

import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TotalUnitQty;

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
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}Retirement" maxOccurs="2" minOccurs="2"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}TotalUnitQty"/>
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
        "retirement",
        "totalUnitQty"
})
@XmlRootElement(name = "Table2aRetirement")
public class Table2ARetirement {

    @XmlElement(name = "Retirement", required = true)
    protected List<Retirement> retirement;
    @XmlElement(name = "TotalUnitQty", namespace = "urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0", required = true)
    protected TotalUnitQty totalUnitQty;

    /**
     * Gets the value of the retirement property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the retirement property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRetirement().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Retirement }
     *
     *
     */
    public List<Retirement> getRetirement() {
        if (retirement == null) {
            retirement = new ArrayList<Retirement>();
        }
        return this.retirement;
    }

    /**
     * Gets the value of the totalUnitQty property.
     *
     * @return
     *     possible object is
     *     {@link TotalUnitQty }
     *
     */
    public TotalUnitQty getTotalUnitQty() {
        return totalUnitQty;
    }

    /**
     * Sets the value of the totalUnitQty property.
     *
     * @param value
     *     allowed object is
     *     {@link TotalUnitQty }
     *
     */
    public void setTotalUnitQty(TotalUnitQty value) {
        this.totalUnitQty = value;
    }

}
