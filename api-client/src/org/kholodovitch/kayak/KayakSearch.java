package org.kholodovitch.kayak;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
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

	public String getResults(int c) throws IOException, HttpException {
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
		return data;
	}

	public String get(String url) throws IOException, HttpException {
		URI uri = URI.create(url);
		DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(8 * 1024);
		BasicHttpRequest request = new BasicHttpRequest("GET", uri.getPath() + "?" + uri.getQuery());
		HttpRequestExecutor httpexecutor = new HttpRequestExecutor();
		HttpCoreContext coreContext = HttpCoreContext.create();
		HttpHost host = new HttpHost(uri.getHost(), 80);
		coreContext.setTargetHost(host);

		for (Entry<String, String> entry : headers.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			request.addHeader(key, value);
		}

		if (!conn.isOpen()) {
			Socket socket = new Socket(host.getHostName(), host.getPort() > 0 ? host.getPort() : 80);
			conn.bind(socket);
		}
		HttpResponse response = httpexecutor.execute(request, conn, coreContext);

		String retval = EntityUtils.toString(response.getEntity());
		conn.close();
		return retval;
	}

	public boolean isComplete(String results) throws Exception {
		if (results == null)
			results = getResults(1);

		Document dom = parseString(results);
		Node morepending = dom.getElementsByTagName("morepending").item(0);
		int count = Integer.parseInt(dom.getElementsByTagName("count").item(0).getChildNodes().item(0).getNodeValue());
		boolean complete = false;

		if (morepending.getChildNodes().getLength() == 0 && count > 0) {
			complete = true;
			this.count = count;
		} else if ((new Date().getTime() - start_time.getTime()) > 60 * 1000) {
			complete = true;
			this.count = count;
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
