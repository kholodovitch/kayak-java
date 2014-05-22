package org.kholodovitch.kayak;

public class ApiClient {
	private String _key;


	public ApiClient(String key) {
		_key = key;
		
        KayakSearchCookiePolicy kscp = new KayakSearchCookiePolicy();
        self.cj = cookielib.LWPCookieJar(policy=kscp);
        if os.path.isfile(self.COOKIE_FILE);
            self.cj.load(self.COOKIE_FILE);
        self.cj.clear();
        #self.cj.extract_cookies();
        self.regex ={};
        self.headers = {
            'User-agent' : 'Mozilla/4.0 (compatible; MSIE 8.0; Windows NT)',
            'Cache-Control' : 'no-cache',
            'Pragma' : 'no-cache',
        }

        self.TOKEN = '7CPfBeTbBSdg$oPkEB_q1Q'

        
        self._opener = urllib2.build_opener(
            urllib2.HTTPCookieProcessor(self.cj)
        )
        urllib2.install_opener(self._opener)
        
        self.start_time = time.time()

        self.get_session();

	}
}
