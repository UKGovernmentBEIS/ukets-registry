package gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0;

import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.AccountType;
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
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}AccountType" maxOccurs="18" minOccurs="18"/>
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
        "accountType",
        "totalUnitQty"
})
@XmlRootElement(name = "Table1")
public class Table1 {

    @XmlElement(name = "AccountType", namespace = "urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0", required = true)
    protected List<AccountType> accountType;
    @XmlElement(name = "TotalUnitQty", namespace = "urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0", required = true)
    protected TotalUnitQty totalUnitQty;

    /**
     * Gets the value of the accountType property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the accountType property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAccountType().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AccountType }
     *
     *
     */
    public List<AccountType> getAccountType() {
        if (accountType == null) {
            accountType = new ArrayList<AccountType>();
        }
        return this.accountType;
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
