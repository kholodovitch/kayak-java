package org.kholodovitch.kayak;

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

import org.kholodovitch.kayak.data.Trip;

@XmlRootElement(name = "searchresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchResult {
	/*
	private static Unmarshaller unmarshaller;

	static {
		try {
			JAXBContext jc = JAXBContext.newInstance(SearchResult.class);
			unmarshaller = jc.createUnmarshaller();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static SearchResult parse(String xml) throws JAXBException {
		return (SearchResult) unmarshaller.unmarshal(new ByteArrayInputStream(xml.getBytes()));
	}
	*/

	@XmlAttribute(name = "searchinstance")
	public String SearchInstance;

	@XmlAttribute(name = "searchid")
	public String SearchId;

	@XmlAttribute(name = "count")
	public int Count;

	@XmlAttribute(name = "morepending")
	public String MorePending;

	//@XmlElementWrapper(name = "trips")
	//@XmlElement(name = "trip")
	//public Trip[] Trips;
}
