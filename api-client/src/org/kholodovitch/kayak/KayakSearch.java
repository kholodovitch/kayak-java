package org.kholodovitch.kayak;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class KayakSearch {
	private final String TOKEN = "7CPfBeTbBSdg$oPkEB_q1Q";
	private final KayakSearchCookiePolicy kscp;
	private final Map<String, String> headers;

	private Date start_time;
	private String sid;
	private String searchid;

	int count;

	public KayakSearch() throws Exception {
		kscp = new KayakSearchCookiePolicy();
		headers = new HashMap<String, String>() {
			private static final long serialVersionUID = 6765904052311476539L;
			{
				put("User-agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT)");
				put("Cache-Control", "no-cache");
				put("Pragma", "no-cache");
			}
		};

		start_time = new Date();
		getSession();
	}

	public void getSession() throws Exception {
		String url = "http://api.kayak.com/k/ident/apisession?token=" + TOKEN;
		String response_xml = get(url);
		Document dom = parseString(response_xml);

		sid = dom.getElementsByTagName("sid").item(0).getChildNodes().item(0).getNodeValue();
	}

	public void startSearch(final SearchRequest request) throws Exception {
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

	public SearchResult getResults(int c) throws IOException, HttpException, JAXBException {
		final int localC = c < 1 ? 1 : c;

		String url = "http://api.kayak.com/s/basic/flight";
		String values = urlencode(new HashMap<String, String>() {
			private static final long serialVersionUID = 1L;
			{
				put("searchid", searchid);
				put("apimode", "1");
				put("c", localC + "");
				put("m", "normal");
				put("d", "up");
				put("s", "price");
				put("_sid_", sid);
				put("version", "1");

			}
		});

		String data = get(url + "?" + values);
		return SearchResult.parse(data);
	}

	public String get(String urlToRead) throws IOException, HttpException {
		URL url;
		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		StringBuilder result = new StringBuilder(500 * 1000);
		try {
			url = new URL(urlToRead);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}

			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
				result.append(line);
				result.append('\n');
			}
			rd.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}

	public boolean isComplete(SearchResult results) throws Exception {
		if (results == null)
			results = getResults(1);

		boolean complete = false;
		boolean isEmptyMorePending = results.MorePending == null || results.MorePending.length() == 0;
		if (isEmptyMorePending && results.Count > 0) {
			complete = true;
			this.count = results.Count;
		} else if ((new Date().getTime() - start_time.getTime()) > 60 * 1000) {
			complete = true;
			this.count = results.Count;
		}
		return complete;
	}

	private Document parseString(String response_xml) throws Exception {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		return docBuilder.parse(new InputSource(new StringReader(response_xml)));
	}

	private String urlencode(Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			if (sb.length() > 0) {
				sb.append("&");
			}
			String keyUtf = urlEncodeUTF8(entry.getKey().toString());
			String valueUtf = urlEncodeUTF8(entry.getValue().toString());
			sb.append(String.format("%s=%s", keyUtf, valueUtf));
		}
		return sb.toString();
	}

	static String urlEncodeUTF8(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new UnsupportedOperationException(e);
		}
	}
}
