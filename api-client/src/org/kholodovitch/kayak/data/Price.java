package org.kholodovitch.kayak.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
public class Price {

    @XmlValue
    private String price = null;

    @XmlAttribute(name="url")
    private String url = null;

    @XmlAttribute(name="currency")
    private String currency = null;

}