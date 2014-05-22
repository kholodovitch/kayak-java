package org.kholodovitch.kayak;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpException;

public class ApiClient {
	public static void main(String[] args) throws Exception {

		JAXBContext jc = JAXBContext.newInstance(SearchResult.class);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		File xml = new File("output.xml");
		SearchResult f = (SearchResult) unmarshaller.unmarshal(xml);
		
		System.out.println(f.Count);
		
		test();
	}

	protected static void test() throws Exception, IOException, HttpException, JAXBException, FileNotFoundException, UnsupportedEncodingException, InterruptedException {
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
					//SearchResult result = SearchResult.parse(search_result_raw_data);
					//System.out.println(result.Count);
				}

				PrintWriter writer = new PrintWriter("output.xml", "UTF-8");
				writer.print(search_result_raw_data);
				writer.close();
			}
			Thread.sleep(5 * 1000);
		}
	}
}
