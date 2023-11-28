package gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0;

import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SubTotal;

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
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}PPSRTransfer" maxOccurs="unbounded" minOccurs="0"/>
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
        "ppsrTransfer",
        "subTotal"
})
@XmlRootElement(name = "Table2c")
public class Table2C {

    @XmlElement(name = "PPSRTransfer")
    protected List<PPSRTransfer> ppsrTransfer;
    @XmlElement(name = "SubTotal", namespace = "urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0", required = true)
    protected SubTotal subTotal;

    /**
     * Gets the value of the ppsrTransfer property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ppsrTransfer property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPPSRTransfer().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PPSRTransfer }
     *
     *
     */
    public List<PPSRTransfer> getPPSRTransfer() {
        if (ppsrTransfer == null) {
            ppsrTransfer = new ArrayList<PPSRTransfer>();
        }
        return this.ppsrTransfer;
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
