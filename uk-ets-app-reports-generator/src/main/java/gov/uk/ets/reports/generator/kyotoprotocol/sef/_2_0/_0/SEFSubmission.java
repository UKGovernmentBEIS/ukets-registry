package gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0;

import jakarta.xml.bind.annotation.*;

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
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}Header"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}Table1"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}Table2a"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}Table2aRetirement"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}Table2b"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}Table2c"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}Table2d"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}Table2e"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}Table3"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}Table4"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}Table5a"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}Table5b"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}Table5c"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}Table5d"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}Table5e"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}Table6a"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}Table6b"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}Table6c"/>
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
        "header",
        "table1",
        "table2A",
        "table2ARetirement",
        "table2B",
        "table2C",
        "table2D",
        "table2E",
        "table3",
        "table4",
        "table5A",
        "table5B",
        "table5C",
        "table5D",
        "table5E",
        "table6A",
        "table6B",
        "table6C"
})
@XmlRootElement(name = "SEFSubmission")
public class SEFSubmission {

    @XmlElement(name = "Header", required = true)
    protected Header header;
    @XmlElement(name = "Table1", required = true)
    protected Table1 table1;
    @XmlElement(name = "Table2a", required = true)
    protected Table2A table2A;
    @XmlElement(name = "Table2aRetirement", required = true)
    protected Table2ARetirement table2ARetirement;
    @XmlElement(name = "Table2b", required = true)
    protected Table2B table2B;
    @XmlElement(name = "Table2c", required = true)
    protected Table2C table2C;
    @XmlElement(name = "Table2d", required = true)
    protected Table2D table2D;
    @XmlElement(name = "Table2e", required = true)
    protected Table2E table2E;
    @XmlElement(name = "Table3", required = true)
    protected Table3 table3;
    @XmlElement(name = "Table4", required = true)
    protected Table4 table4;
    @XmlElement(name = "Table5a", required = true)
    protected Table5A table5A;
    @XmlElement(name = "Table5b", required = true)
    protected Table5B table5B;
    @XmlElement(name = "Table5c", required = true)
    protected Table5C table5C;
    @XmlElement(name = "Table5d", required = true)
    protected Table5D table5D;
    @XmlElement(name = "Table5e", required = true)
    protected Table5E table5E;
    @XmlElement(name = "Table6a", required = true)
    protected Table6A table6A;
    @XmlElement(name = "Table6b", required = true)
    protected Table6B table6B;
    @XmlElement(name = "Table6c", required = true)
    protected Table6C table6C;

    /**
     * Gets the value of the header property.
     *
     * @return
     *     possible object is
     *     {@link Header }
     *
     */
    public Header getHeader() {
        return header;
    }

    /**
     * Sets the value of the header property.
     *
     * @param value
     *     allowed object is
     *     {@link Header }
     *
     */
    public void setHeader(Header value) {
        this.header = value;
    }

    /**
     * Gets the value of the table1 property.
     *
     * @return
     *     possible object is
     *     {@link Table1 }
     *
     */
    public Table1 getTable1() {
        return table1;
    }

    /**
     * Sets the value of the table1 property.
     *
     * @param value
     *     allowed object is
     *     {@link Table1 }
     *
     */
    public void setTable1(Table1 value) {
        this.table1 = value;
    }

    /**
     * Gets the value of the table2A property.
     *
     * @return
     *     possible object is
     *     {@link Table2A }
     *
     */
    public Table2A getTable2A() {
        return table2A;
    }

    /**
     * Sets the value of the table2A property.
     *
     * @param value
     *     allowed object is
     *     {@link Table2A }
     *
     */
    public void setTable2A(Table2A value) {
        this.table2A = value;
    }

    /**
     * Gets the value of the table2ARetirement property.
     *
     * @return
     *     possible object is
     *     {@link Table2ARetirement }
     *
     */
    public Table2ARetirement getTable2ARetirement() {
        return table2ARetirement;
    }

    /**
     * Sets the value of the table2ARetirement property.
     *
     * @param value
     *     allowed object is
     *     {@link Table2ARetirement }
     *
     */
    public void setTable2ARetirement(Table2ARetirement value) {
        this.table2ARetirement = value;
    }

    /**
     * Gets the value of the table2B property.
     *
     * @return
     *     possible object is
     *     {@link Table2B }
     *
     */
    public Table2B getTable2B() {
        return table2B;
    }

    /**
     * Sets the value of the table2B property.
     *
     * @param value
     *     allowed object is
     *     {@link Table2B }
     *
     */
    public void setTable2B(Table2B value) {
        this.table2B = value;
    }

    /**
     * Gets the value of the table2C property.
     *
     * @return
     *     possible object is
     *     {@link Table2C }
     *
     */
    public Table2C getTable2C() {
        return table2C;
    }

    /**
     * Sets the value of the table2C property.
     *
     * @param value
     *     allowed object is
     *     {@link Table2C }
     *
     */
    public void setTable2C(Table2C value) {
        this.table2C = value;
    }

    /**
     * Gets the value of the table2D property.
     *
     * @return
     *     possible object is
     *     {@link Table2D }
     *
     */
    public Table2D getTable2D() {
        return table2D;
    }

    /**
     * Sets the value of the table2D property.
     *
     * @param value
     *     allowed object is
     *     {@link Table2D }
     *
     */
    public void setTable2D(Table2D value) {
        this.table2D = value;
    }

    /**
     * Gets the value of the table2E property.
     *
     * @return
     *     possible object is
     *     {@link Table2E }
     *
     */
    public Table2E getTable2E() {
        return table2E;
    }

    /**
     * Sets the value of the table2E property.
     *
     * @param value
     *     allowed object is
     *     {@link Table2E }
     *
     */
    public void setTable2E(Table2E value) {
        this.table2E = value;
    }

    /**
     * Gets the value of the table3 property.
     *
     * @return
     *     possible object is
     *     {@link Table3 }
     *
     */
    public Table3 getTable3() {
        return table3;
    }

    /**
     * Sets the value of the table3 property.
     *
     * @param value
     *     allowed object is
     *     {@link Table3 }
     *
     */
    public void setTable3(Table3 value) {
        this.table3 = value;
    }

    /**
     * Gets the value of the table4 property.
     *
     * @return
     *     possible object is
     *     {@link Table4 }
     *
     */
    public Table4 getTable4() {
        return table4;
    }

    /**
     * Sets the value of the table4 property.
     *
     * @param value
     *     allowed object is
     *     {@link Table4 }
     *
     */
    public void setTable4(Table4 value) {
        this.table4 = value;
    }

    /**
     * Gets the value of the table5A property.
     *
     * @return
     *     possible object is
     *     {@link Table5A }
     *
     */
    public Table5A getTable5A() {
        return table5A;
    }

    /**
     * Sets the value of the table5A property.
     *
     * @param value
     *     allowed object is
     *     {@link Table5A }
     *
     */
    public void setTable5A(Table5A value) {
        this.table5A = value;
    }

    /**
     * Gets the value of the table5B property.
     *
     * @return
     *     possible object is
     *     {@link Table5B }
     *
     */
    public Table5B getTable5B() {
        return table5B;
    }

    /**
     * Sets the value of the table5B property.
     *
     * @param value
     *     allowed object is
     *     {@link Table5B }
     *
     */
    public void setTable5B(Table5B value) {
        this.table5B = value;
    }

    /**
     * Gets the value of the table5C property.
     *
     * @return
     *     possible object is
     *     {@link Table5C }
     *
     */
    public Table5C getTable5C() {
        return table5C;
    }

    /**
     * Sets the value of the table5C property.
     *
     * @param value
     *     allowed object is
     *     {@link Table5C }
     *
     */
    public void setTable5C(Table5C value) {
        this.table5C = value;
    }

    /**
     * Gets the value of the table5D property.
     *
     * @return
     *     possible object is
     *     {@link Table5D }
     *
     */
    public Table5D getTable5D() {
        return table5D;
    }

    /**
     * Sets the value of the table5D property.
     *
     * @param value
     *     allowed object is
     *     {@link Table5D }
     *
     */
    public void setTable5D(Table5D value) {
        this.table5D = value;
    }

    /**
     * Gets the value of the table5E property.
     *
     * @return
     *     possible object is
     *     {@link Table5E }
     *
     */
    public Table5E getTable5E() {
        return table5E;
    }

    /**
     * Sets the value of the table5E property.
     *
     * @param value
     *     allowed object is
     *     {@link Table5E }
     *
     */
    public void setTable5E(Table5E value) {
        this.table5E = value;
    }

    /**
     * Gets the value of the table6A property.
     *
     * @return
     *     possible object is
     *     {@link Table6A }
     *
     */
    public Table6A getTable6A() {
        return table6A;
    }

    /**
     * Sets the value of the table6A property.
     *
     * @param value
     *     allowed object is
     *     {@link Table6A }
     *
     */
    public void setTable6A(Table6A value) {
        this.table6A = value;
    }

    /**
     * Gets the value of the table6B property.
     *
     * @return
     *     possible object is
     *     {@link Table6B }
     *
     */
    public Table6B getTable6B() {
        return table6B;
    }

    /**
     * Sets the value of the table6B property.
     *
     * @param value
     *     allowed object is
     *     {@link Table6B }
     *
     */
    public void setTable6B(Table6B value) {
        this.table6B = value;
    }

    /**
     * Gets the value of the table6C property.
     *
     * @return
     *     possible object is
     *     {@link Table6C }
     *
     */
    public Table6C getTable6C() {
        return table6C;
    }

    /**
     * Sets the value of the table6C property.
     *
     * @param value
     *     allowed object is
     *     {@link Table6C }
     *
     */
    public void setTable6C(Table6C value) {
        this.table6C = value;
    }

}
