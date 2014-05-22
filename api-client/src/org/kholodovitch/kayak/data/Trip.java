package org.kholodovitch.kayak.data;

import java.io.ByteArrayInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "trip")
@XmlAccessorType(XmlAccessType.FIELD)
public class Trip {
	private static Unmarshaller unmarshaller;

	static{
		try {
			JAXBContext jc = JAXBContext.newInstance(Trip.class);
			unmarshaller = jc.createUnmarshaller();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Trip parse(String xml) throws JAXBException
	{
		return (Trip) unmarshaller.unmarshal( new ByteArrayInputStream( xml.getBytes()));
	}
	
	@XmlAttribute(name = "id")
	public String Id;
	
	@XmlAttribute(name = "price")
	public Price price;
	
	@XmlElementWrapper(name = "legs")
	@XmlElement(name = "leg")
	public Leg[] Legs;
}