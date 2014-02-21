package edu.unibamberg.hamann.evalTags.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

/**
 * 
 * Helper Class for HTTP gets (reduced version)
 * 
 * @author Denis Hamann
 * @version 28.05.2013
 * @license EUPL
 * 
 */
public class HttpHelper {

	/**
	 * 
	 * @param surl
	 *            - {@link String} of the URL to be accessed
	 * @return {@link String} of the Pagecontent
	 */
	public static String getPage(String surl) {

		// Proxy proxy = new Proxy(Proxy.Type.HTTP, new
		// InetSocketAddress("proxy.com", 80));

		String sPage = "";

		URLConnection connection = null;

		try {
			URL urlpage = new URL(surl);
			//open connection
			connection = urlpage.openConnection();
			// connection = urlpage.openConnection(proxy);
			//follow redirects
			connection.setDoOutput(true);
			//initaite the buffer
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			String line = null;

			//cat the output, insert line seperators based on the OS environment
			while ((line = rd.readLine()) != null) {
				sPage += line + System.getProperty("line.separator");
			}

		} catch (ConnectException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sPage;
	}
}