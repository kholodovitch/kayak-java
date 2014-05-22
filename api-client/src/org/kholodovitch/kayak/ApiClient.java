package org.kholodovitch.kayak;

public class ApiClient {
	public static void main(String[] args) throws Exception {
		KayakSearch ks = new KayakSearch();
		SearchRequest request = new SearchRequest();
		request.setOrig("SLC");
		request.setDest("AMM");
		request.setOneway(false);
		request.setDepartDate("05/26/2014");
		request.setReturnDate("05/28/2014");
		ks.startSearch(request);

		boolean complete = false;
		boolean success = false;

		while (!complete) {
			String iter_results = ks.getResults(1);
			if (ks.isComplete(iter_results)) {
				complete = true;
				String search_result_raw_data = ks.getResults(ks.count);
				if (search_result_raw_data != null) {
					System.out.println(search_result_raw_data);
				}

			}
			Thread.sleep(5 * 1000);
		}
	}
}
