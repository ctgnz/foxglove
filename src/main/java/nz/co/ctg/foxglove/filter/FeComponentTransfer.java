package nz.co.ctg.foxglove.filter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "feFuncR",
    "feFuncG",
    "feFuncB",
    "feFuncA"
})
@XmlRootElement(name = "feComponentTransfer")
public class FeComponentTransfer extends AbstractSvgFilterPrimitive {

    @XmlAttribute(name = "x")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String x;

    @XmlAttribute(name = "y")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String y;

    @XmlAttribute(name = "width")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String width;

    @XmlAttribute(name = "height")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String height;

    @XmlAttribute(name = "result")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String result;

    @XmlAttribute(name = "in")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String in;
    protected FeFuncR feFuncR;
    protected FeFuncG feFuncG;
    protected FeFuncB feFuncB;
    protected FeFuncA feFuncA;

    /**
     * Gets the value of the x property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getX() {
        return x;
    }

    /**
     * Sets the value of the x property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setX(String value) {
        this.x = value;
    }

    /**
     * Gets the value of the y property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getY() {
        return y;
    }

    /**
     * Sets the value of the y property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setY(String value) {
        this.y = value;
    }

    /**
     * Gets the value of the width property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setWidth(String value) {
        this.width = value;
    }

    /**
     * Gets the value of the height property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHeight(String value) {
        this.height = value;
    }

    /**
     * Gets the value of the result property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setResult(String value) {
        this.result = value;
    }

    /**
     * Gets the value of the in property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIn() {
        return in;
    }

    /**
     * Sets the value of the in property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIn(String value) {
        this.in = value;
    }

    /**
     * Gets the value of the feFuncR property.
     *
     * @return
     *     possible object is
     *     {@link FeFuncR }
     *
     */
    public FeFuncR getFeFuncR() {
        return feFuncR;
    }

    /**
     * Sets the value of the feFuncR property.
     *
     * @param value
     *     allowed object is
     *     {@link FeFuncR }
     *
     */
    public void setFeFuncR(FeFuncR value) {
        this.feFuncR = value;
    }

    /**
     * Gets the value of the feFuncG property.
     *
     * @return
     *     possible object is
     *     {@link FeFuncG }
     *
     */
    public FeFuncG getFeFuncG() {
        return feFuncG;
    }

    /**
     * Sets the value of the feFuncG property.
     *
     * @param value
     *     allowed object is
     *     {@link FeFuncG }
     *
     */
    public void setFeFuncG(FeFuncG value) {
        this.feFuncG = value;
    }

    /**
     * Gets the value of the feFuncB property.
     *
     * @return
     *     possible object is
     *     {@link FeFuncB }
     *
     */
    public FeFuncB getFeFuncB() {
        return feFuncB;
    }

    /**
     * Sets the value of the feFuncB property.
     *
     * @param value
     *     allowed object is
     *     {@link FeFuncB }
     *
     */
    public void setFeFuncB(FeFuncB value) {
        this.feFuncB = value;
    }

    /**
     * Gets the value of the feFuncA property.
     *
     * @return
     *     possible object is
     *     {@link FeFuncA }
     *
     */
    public FeFuncA getFeFuncA() {
        return feFuncA;
    }

    /**
     * Sets the value of the feFuncA property.
     *
     * @param value
     *     allowed object is
     *     {@link FeFuncA }
     *
     */
    public void setFeFuncA(FeFuncA value) {
        this.feFuncA = value;
    }

}
