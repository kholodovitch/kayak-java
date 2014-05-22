package org.kholodovitch.kayak.data;

public class LatLon {
	private float lat;
	private float lon;

	public LatLon(float lat, float lon) {
		this.lat = lat;
		this.lon = lon;
	}

	public float getLat() {
		return lat;
	}

	public float getLon() {
		return lon;
	}
}
