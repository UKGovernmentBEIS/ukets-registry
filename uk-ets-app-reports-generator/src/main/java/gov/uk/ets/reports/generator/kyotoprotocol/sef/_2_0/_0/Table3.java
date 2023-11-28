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
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}TransactionOrEventType" maxOccurs="8" minOccurs="8"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}TotalTxOrEvent"/>
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
        "transactionOrEventType",
        "totalTxOrEvent"
})
@XmlRootElement(name = "Table3")
public class Table3 {

    @XmlElement(name = "TransactionOrEventType", required = true)
    protected List<TransactionOrEventType> transactionOrEventType;
    @XmlElement(name = "TotalTxOrEvent", required = true)
    protected TotalTxOrEvent totalTxOrEvent;

    /**
     * Gets the value of the transactionOrEventType property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the transactionOrEventType property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransactionOrEventType().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransactionOrEventType }
     *
     *
     */
    public List<TransactionOrEventType> getTransactionOrEventType() {
        if (transactionOrEventType == null) {
            transactionOrEventType = new ArrayList<TransactionOrEventType>();
        }
        return this.transactionOrEventType;
    }

    /**
     * Gets the value of the totalTxOrEvent property.
     *
     * @return
     *     possible object is
     *     {@link TotalTxOrEvent }
     *
     */
    public TotalTxOrEvent getTotalTxOrEvent() {
        return totalTxOrEvent;
    }

    /**
     * Sets the value of the totalTxOrEvent property.
     *
     * @param value
     *     allowed object is
     *     {@link TotalTxOrEvent }
     *
     */
    public void setTotalTxOrEvent(TotalTxOrEvent value) {
        this.totalTxOrEvent = value;
    }

}
