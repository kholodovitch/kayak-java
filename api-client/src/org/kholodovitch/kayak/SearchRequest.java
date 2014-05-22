package org.kholodovitch.kayak;

public class SearchRequest {

	private boolean isOneway;
	private String orig;
	private String dest;
	private String departDate;
	private String returnDate;
	private String depart_time;
	private String return_time;
	private int travelers;
	private String cabin;

	public SearchRequest() {
		depart_time = "a";
		return_time = "a";
		travelers = 1;
		cabin = "e";
	}

	public boolean isOneway() {
		return isOneway;
	}

	public String getOrig() {
		return orig;
	}

	public String getDest() {
		return dest;
	}

	public String getDepartDate() {
		return departDate;
	}

	public String getReturnDate() {
		return returnDate;
	}

	public String getDepartTime() {
		return depart_time;
	}

	public String getReturnTime() {
		return return_time;
	}

	public int getTravelers() {
		return travelers;
	}

	public String getCabin() {
		return cabin;
	}

}
