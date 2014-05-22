package org.kholodovitch.kayak;

import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class KayakSearch {
	private final String TOKEN = "7CPfBeTbBSdg$oPkEB_q1Q"; 
	private final KayakSearchCookiePolicy kscp;
	private final Map<String, String> headers;

	private Date start_time;
	private String sid;
	private String searchid;

	public KayakSearch() {
		kscp = new KayakSearchCookiePolicy();
/*
        self.cj = cookielib.LWPCookieJar(policy=kscp)
        if os.path.isfile(self.COOKIE_FILE):
            self.cj.load(self.COOKIE_FILE)
        self.cj.clear()
        #self.cj.extract_cookies()
        self.regex ={}
*/
		headers = new HashMap<String, String>() {
			private static final long serialVersionUID = 6765904052311476539L;
			{
				put("User-agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT)");
				put("Cache-Control", "no-cache");
				put("Pragma", "no-cache");
			}
		};
        
/*
        self._opener = urllib2.build_opener(
            urllib2.HTTPCookieProcessor(self.cj)
        )
        urllib2.install_opener(self._opener)
 */
		
		start_time = new Date();
		getSession();
	}

	public void getSession() {
        String url = "http://api.kayak.com/k/ident/apisession?token=" + TOKEN;
        String response_xml = get(url);
        Document dom = parseString(response_xml);
        
        sid = dom.getElementsByTagName("sid").item(0).getChildNodes().item(0).getNodeValue();                
	}

	public void startSearch(final SearchRequest request) {
		String url = "http://api.kayak.com/s/apisearch";
		String values = urlencode(new HashMap<String, String>() {
			private static final long serialVersionUID = -7916833811938086188L;
			{
				put("basicmode", "true");
				put("oneway", request.isOneway() ? "y" : "n");
				put("origin", request.getOrig());
				put("destination", request.getDest());
				put("depart_date", request.getDepartDate());
				put("return_date", request.getReturnDate());
				put("depart_time", request.getDepartTime());
				put("return_time", request.getReturnTime());
				put("travelers", request.getTravelers() + "");
				put("cabin", request.getCabin());
				put("action", "doFlights");
				put("apimode", "1");
				put("_sid_", sid);
				put("version", "1");
			}
		});
		
        String data = get(url + '?' + values);
        Document dom = parseString(data);
        searchid = dom.getElementsByTagName("searchid").item(0).getChildNodes().item(0).getNodeValue();
	}

	public int getResults(int c) {
		if (c < 1)
			c = 1;
	}

	public String get(String url) {
	}
	
	public boolean isComplete(Object results)
	{}

	private Document parseString(String response_xml) {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		return docBuilder.parse(new InputSource(new StringReader(theString)));
	}

	private String urlencode(Map<String, String> values) {
		return null;
	}
}
