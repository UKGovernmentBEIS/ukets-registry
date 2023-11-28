package gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0;

import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SubTotal;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionType;

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
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}TransactionType" maxOccurs="17" minOccurs="17"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}SubTotal"/>
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
        "subTotal"
})
@XmlRootElement(name = "Table2a")
public class Table2A {

    @XmlElement(name = "TransactionType", namespace = "urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0", required = true)
    protected List<TransactionType> transactionType;
    @XmlElement(name = "SubTotal", namespace = "urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0", required = true)
    protected SubTotal subTotal;

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

}
