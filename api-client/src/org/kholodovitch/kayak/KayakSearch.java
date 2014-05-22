package org.kholodovitch.kayak;

public class KayakSearch {
	public KayakSearch() {
	}

	public int getSession() {
	}

	public int start_search(String orig, String dest, boolean oneway, String depart_date, String return_date, String depart_time, String return_time, int travelers, String cabin) {
		if (depart_time == null)
			depart_time = "a";
		if (return_time == null)
			return_time = "a";
		if (travelers < 1)
			travelers = 1;
		if (cabin == null)
			cabin = "e";
	}

	public int getResults(int c) {
		if (c < 1)
			c = 1;
	}

	public int get(String url) {
	}
	
	public boolean isComplete(Object results)
	{}
}
