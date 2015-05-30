/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cjohnson.mywxstickread;

import com.cjohnson.mywxstick.model.Observation;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 *
 * @author cjohnson
 */
public class ArduinoObservationHandler extends ArduinoTextLineHandler {
	public final static Logger logger = LoggerFactory.getLogger(ArduinoObservationHandler.class);

	public ArduinoObservationHandler (BlockingQueue q) {
		super (q);
	}
	
	@Override
	public void handleData(String line) {
		logger.debug(line);
		
		if (! ArduinoLineSanityCheck.sanityCheck(line)) {
			logger.info("Invalid line: [" + line + "]");
			return;
		}
		
		// Attempt to make an observation
		Observation ob = Observation.MakeObservationFromJSON (line);
		
		if (ob == null) {
			logger.info("Observation Factory failed from json: [" + line + "]");
			return;			
		}
		

		switch (ob.getObservationType())
		{
			case INSTANTANEOUS_OBSERVATION:
				return;
			default:
				break;
		}
		
		StringEntity postEntity = new StringEntity(line, ContentType.create("text/json", "UTF-8"));
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost("http://localhost:8084/mywxstick/observice/addob/test/");
		httpPost.setEntity(postEntity);

		ResponseHandler<String> rh = new ResponseHandler<String>() {

			@Override
			public String handleResponse(final HttpResponse response) throws IOException {
				StatusLine statusLine = response.getStatusLine();
				HttpEntity entity = response.getEntity();
				if (statusLine.getStatusCode() >= 300) {
					throw new HttpResponseException(
							statusLine.getStatusCode(),
							statusLine.getReasonPhrase());
				}
				if (entity == null) {
					throw new ClientProtocolException("Response contains no content");
				}
		//        Gson gson = new GsonBuilder().create();
		//        ContentType contentType = ContentType.getOrDefault(entity);
		//        Charset charset = contentType.getCharset();
		//        Reader reader = new InputStreamReader(entity.getContent(), charset);
		//        return gson.fromJson(reader, MyJsonObject.class);
				return (EntityUtils.toString(entity));
			}
		};

		try {
			String responseString = httpclient.execute(httpPost, rh);
		} catch (IOException ex) {
			logger.equals(ex);
		}
	}	
}
