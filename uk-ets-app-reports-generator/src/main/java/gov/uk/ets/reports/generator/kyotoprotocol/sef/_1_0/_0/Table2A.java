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
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}TransactionType" maxOccurs="13" minOccurs="13"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}SubTotal"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}Retirement"/>
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
        "transactionType",
        "subTotal",
        "retirement"
})
@XmlRootElement(name = "Table2a")
public class Table2A {

    @XmlElement(name = "TransactionType", required = true)
    protected List<TransactionType> transactionType;
    @XmlElement(name = "SubTotal", required = true)
    protected SubTotal subTotal;
    @XmlElement(name = "Retirement", required = true)
    protected Retirement retirement;

    /**
     * Gets the value of the transactionType property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the transactionType property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransactionType().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransactionType }
     *
     *
     */
    public List<TransactionType> getTransactionType() {
        if (transactionType == null) {
            transactionType = new ArrayList<TransactionType>();
        }
        return this.transactionType;
    }

    /**
     * Gets the value of the subTotal property.
     *
     * @return
     *     possible object is
     *     {@link SubTotal }
     *
     */
    public SubTotal getSubTotal() {
        return subTotal;
    }

    /**
     * Sets the value of the subTotal property.
     *
     * @param value
     *     allowed object is
     *     {@link SubTotal }
     *
     */
    public void setSubTotal(SubTotal value) {
        this.subTotal = value;
    }

    /**
     * Gets the value of the retirement property.
     *
     * @return
     *     possible object is
     *     {@link Retirement }
     *
     */
    public Retirement getRetirement() {
        return retirement;
    }

    /**
     * Sets the value of the retirement property.
     *
     * @param value
     *     allowed object is
     *     {@link Retirement }
     *
     */
    public void setRetirement(Retirement value) {
        this.retirement = value;
    }

}
