package com.sumi.transaku.core.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.sumi.transaku.core.configs.SmsConfig;
import com.sumi.transaku.core.repositories.ApplicationPropertiesRepository;


@Service
public class SmsService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SmsService.class);
	
	@Autowired
	ApplicationPropertiesRepository propertiesRepository;
	@Autowired
	SmsConfig smsConfig;
	

	@Async
	public void sendSms(String recipient, String content) throws Exception {

//		String url = StaticFields.SMS_MASKING_URL_VALUE;
/*		url = url.replace("[userkey]", StaticFields.SMS_MASKING_USER_KEY_VALUE).replace("[passkey]", StaticFields.SMS_MASKING_PASS_KEY_VALUE)
				.replace("[nohp]", recipient).replace("[pesan]", content);
*/		
		String url = smsConfig.getSmsMaskingUrl();
		url = url.replace("[userkey]", smsConfig.getSmsMaskingUserKey()).replace("[passkey]", smsConfig.getSmsMaskingPassKey())
				.replace("[nohp]", recipient).replace("[pesan]", URLEncoder.encode(content, "UTF-8"));

		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		//con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		
		LOGGER.info("\nSending 'GET' request to URL : " + url);
		LOGGER.info("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		//LOGGER.info(response.toString());

	}
	
	
	/*public void sendSms(String recipient, String content){
	URL url;
	try {
		String strUrl = StaticFields.SMS_MASKING_URL_VALUE;
		strUrl.replace("[userkey]", StaticFields.SMS_MASKING_USER_KEY_VALUE);
		strUrl.replace("[passkey]", StaticFields.SMS_MASKING_PASS_KEY_VALUE);
		strUrl.replace("[recipient]", recipient);
		strUrl.replace("[content]", content);
		
		url = new URL(URLEncoder.encode(strUrl));
	 
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
		StringBuilder sb = new StringBuilder();
	    for (String line; (line = reader.readLine()) != null;) {
	    	sb.append(line);
//	        System.out.println(line);
	    }
	    LOGGER.info(sb.toString());
	    
	} catch (MalformedURLException e) {
		LOGGER.error(e.getMessage());
		e.printStackTrace();
	} catch (UnsupportedEncodingException e) {
		LOGGER.error(e.getMessage());
		e.printStackTrace();
	} catch (IOException e) {
		LOGGER.error(e.getMessage());
		e.printStackTrace();
	}
}*/
	
	/*private String sendPost(String payload) throws Exception {

		String url = StaticFields.SMS_MASKING_URL_VALUE;
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/xml");

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(payload);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());
		return response.toString();
	}*/
	
}
