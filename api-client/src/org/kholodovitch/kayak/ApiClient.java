package org.kholodovitch.kayak;

public class ApiClient {
	public static void main(String[] args) throws Exception {
		SearchRequest request = new SearchRequest();
		request.setOrig("SLC");
		request.setDest("AMM");
		request.setOneway(false);
		request.setDepartDate("05/26/2014");
		request.setReturnDate("05/28/2014");

		SearchResult result = process(request);
		System.out.println(result.Trips.length);
	}

	protected static SearchResult process(SearchRequest request) throws Exception {
		KayakSearch ks = new KayakSearch();
		ks.startSearch(request);

		boolean complete = false;
		while (!complete) {
			SearchResult iter_results = ks.getResults(1);
			if (ks.isComplete(iter_results)) {
				complete = true;
				return ks.getResults(ks.count);
			}
			Thread.sleep(10 * 1000);
		}

		return null;
	}
}
