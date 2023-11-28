package gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0;

import javax.xml.bind.annotation.*;

/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0>UnitQtyType">
 *       &lt;attribute name="type" use="required" type="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}UnitTypeEnum" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "value"
})
@XmlRootElement(name = "UnitQty")
public class UnitQty {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "type", required = true)
    protected UnitTypeEnum type;

    /**
     * Gets the value of the value property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the type property.
     *
     * @return
     *     possible object is
     *     {@link UnitTypeEnum }
     *
     */
    public UnitTypeEnum getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     *
     * @param value
     *     allowed object is
     *     {@link UnitTypeEnum }
     *
     */
    public void setType(UnitTypeEnum value) {
        this.type = value;
    }

}