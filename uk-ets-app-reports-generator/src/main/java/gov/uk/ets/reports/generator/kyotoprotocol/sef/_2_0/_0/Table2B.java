package gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0;

import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SubTotal;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.ExternalTransfer;

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
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}ExternalTransfer" maxOccurs="unbounded" minOccurs="0"/>
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
        "externalTransfer",
        "subTotal"
})
@XmlRootElement(name = "Table2b")
public class Table2B {

    @XmlElement(name = "ExternalTransfer", namespace = "urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0")
    protected List<ExternalTransfer> externalTransfer;
    @XmlElement(name = "SubTotal", namespace = "urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0", required = true)
    protected SubTotal subTotal;

    /**
     * Gets the value of the externalTransfer property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the externalTransfer property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExternalTransfer().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExternalTransfer }
     *
     *
     */
    public List<ExternalTransfer> getExternalTransfer() {
        if (externalTransfer == null) {
            externalTransfer = new ArrayList<ExternalTransfer>();
        }
        return this.externalTransfer;
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
