//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.12.16 at 05:01:18 PM GMT 
//


package org.deegree.portal.cataloguemanager.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element ref="{http://www.isotc211.org/2005/gmd}description"/>
 *         &lt;element ref="{http://www.isotc211.org/2005/gmd}geographicElement"/>
 *         &lt;element ref="{http://www.isotc211.org/2005/gmd}temporalElement"/>
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
    "description",
    "geographicElement",
    "temporalElement"
})
@XmlRootElement(name = "EX_Extent")
public class EXExtent {

    @XmlElement(required = true)
    protected Description description;
    @XmlElement(required = true)
    protected GeographicElement geographicElement;
    @XmlElement(required = true)
    protected TemporalElement temporalElement;

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link Description }
     *     
     */
    public Description getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link Description }
     *     
     */
    public void setDescription(Description value) {
        this.description = value;
    }

    /**
     * Gets the value of the geographicElement property.
     * 
     * @return
     *     possible object is
     *     {@link GeographicElement }
     *     
     */
    public GeographicElement getGeographicElement() {
        return geographicElement;
    }

    /**
     * Sets the value of the geographicElement property.
     * 
     * @param value
     *     allowed object is
     *     {@link GeographicElement }
     *     
     */
    public void setGeographicElement(GeographicElement value) {
        this.geographicElement = value;
    }

    /**
     * Gets the value of the temporalElement property.
     * 
     * @return
     *     possible object is
     *     {@link TemporalElement }
     *     
     */
    public TemporalElement getTemporalElement() {
        return temporalElement;
    }

    /**
     * Sets the value of the temporalElement property.
     * 
     * @param value
     *     allowed object is
     *     {@link TemporalElement }
     *     
     */
    public void setTemporalElement(TemporalElement value) {
        this.temporalElement = value;
    }

}
