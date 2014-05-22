package org.kholodovitch.kayak.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Segment {
	@XmlElement(name = "airline")
	public String Airline;

	@XmlElement(name = "flight")
	public String Flight;

	@XmlElement(name = "duration_minutes")
	public String DurationMinutes;

	@XmlElement(name = "equip")
	public String Equip;

	@XmlElement(name = "miles")
	public String Miles;

	@XmlElement(name = "dt")
	public String dt;

	@XmlElement(name = "o")
	public String o;

	@XmlElement(name = "at")
	public String at;

	@XmlElement(name = "d")
	public String d;

	@XmlElement(name = "cabin")
	public String Cabin;
}
