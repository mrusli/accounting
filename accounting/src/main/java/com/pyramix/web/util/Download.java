package com.pyramix.web.util;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;

public class Download {
	
	private static final Logger log = Logger.getLogger(Download.class);
	
	static {
	    final TrustManager[] trustAllCertificates = new TrustManager[] {
	        new X509TrustManager() {
	            @Override
	            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                return null; // Not relevant.
	            }
	            @Override
	            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
	                // Do nothing. Just allow them all.
	            }
	            @Override
	            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
	                // Do nothing. Just allow them all.
	            }
	        }
	    };

	    try {
	        SSLContext sc = SSLContext.getInstance("SSL");
	        sc.init(null, trustAllCertificates, new SecureRandom());
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	        
	        // Create all-trusting host name verifier
	        HostnameVerifier allHostsValid = new HostnameVerifier() {
	            public boolean verify(String hostname, SSLSession session) {
	                return true;
	            }
	        };

	        // Install the all-trusting host verifier
	        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);	        
	    } catch (NoSuchAlgorithmException e) {
	    	e.printStackTrace();
	    } catch (KeyManagementException e) {
	    	e.printStackTrace();
	    }
	}	
	
	public void downloadFile(String urlFilename, String outFilename) throws IOException {
		// URL url = new File(filename).toURI().toURL();
		// log.info("downloading file from: "+url.toString());
		URL url = new URL(urlFilename);
		
		log.info("downloading file from: "+url.toString());
		log.info("export to: "+outFilename);
		
		try (BufferedInputStream in = new BufferedInputStream(url.openStream());
			FileOutputStream fileOutputStream = new FileOutputStream(outFilename)) {
				byte dataBuffer[] = new byte[1024];
				int bytesRead;
				while((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
					fileOutputStream.write(dataBuffer, 0, bytesRead);
				}				
		} catch (IOException e) {
			throw e;
		}	
	}	
}
