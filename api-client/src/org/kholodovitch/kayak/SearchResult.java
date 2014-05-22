package org.kholodovitch.kayak;

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
	@XmlAttribute(name = "searchinstance")
	public String SearchInstance;
	
	@XmlAttribute(name = "searchid")
	public String SearchId;
	
	@XmlAttribute(name = "count")
	public int Count;
	
	@XmlAttribute(name = "morepending")
	public String MorePending;
	
	@XmlElementWrapper(name = "trips")
	@XmlElement(name = "trip")
	public Trip[] Trips;
}
