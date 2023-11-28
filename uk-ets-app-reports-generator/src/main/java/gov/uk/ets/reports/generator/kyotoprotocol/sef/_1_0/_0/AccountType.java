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
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}UnitQty" maxOccurs="6" minOccurs="6"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}AccountTypeEnum" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "unitQty"
})
@XmlRootElement(name = "AccountType")
public class AccountType {

    @XmlElement(name = "UnitQty", required = true)
    protected List<UnitQty> unitQty;
    @XmlAttribute(name = "name", required = true)
    protected AccountTypeEnum name;

    /**
     * Gets the value of the unitQty property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the unitQty property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUnitQty().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UnitQty }
     *
     *
     */
    public List<UnitQty> getUnitQty() {
        if (unitQty == null) {
            unitQty = new ArrayList<UnitQty>();
        }
        return this.unitQty;
    }

    /**
     * Gets the value of the name property.
     *
     * @return
     *     possible object is
     *     {@link AccountTypeEnum }
     *
     */
    public AccountTypeEnum getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value
     *     allowed object is
     *     {@link AccountTypeEnum }
     *
     */
    public void setName(AccountTypeEnum value) {
        this.name = value;
    }

}