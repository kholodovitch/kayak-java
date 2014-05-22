package org.kholodovitch.kayak.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Leg {
	@XmlElement(name = "airline")
	public String Airline;

	@XmlElement(name = "airline_display")
	public String AirlineDisplay;

	@XmlElement(name = "orig")
	public String Original;

	@XmlElement(name = "dest")
	public String Destination;

	@XmlElement(name = "depart")
	public String Department;

	@XmlElement(name = "arrive")
	public String Arrival;

	@XmlElement(name = "stops")
	public String Stops;

	@XmlElement(name = "duration_minutes")
	public String DurationMinutes;

	@XmlElement(name = "cabin")
	public String Cabin;

	@XmlElement(name = "segment")
	public Segment[] Segments;
}