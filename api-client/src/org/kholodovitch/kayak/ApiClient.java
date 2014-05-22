package org.kholodovitch.kayak;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class ApiClient {
	public static void main(String[] args) throws Exception {
		test();
		
		JAXBContext jc = JAXBContext.newInstance(SearchResult.class);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		Marshaller marshaller = jc.createMarshaller();
		SearchResult result = (SearchResult) unmarshaller.unmarshal(new File("bin/output.xml"));
		
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8"); 
		marshaller.marshal(result, new File("bin/reserialize.xml"));
	}

	protected static SearchResult test() throws Exception {
		KayakSearch ks = new KayakSearch();
		SearchRequest request = new SearchRequest();
		request.setOrig("SLC");
		request.setDest("AMM");
		request.setOneway(false);
		request.setDepartDate("05/26/2014");
		request.setReturnDate("05/28/2014");
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
