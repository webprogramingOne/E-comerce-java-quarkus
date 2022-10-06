package com.barrans.master.util;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

public class InternalHTTPUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(InternalHTTPUtil.class);
	
	public static Object postHttp(String url, Object req) {
		try {
			HttpPost request = new HttpPost(url);
			request.setEntity(new StringEntity(new Gson().toJson(req)));
			request.addHeader("content-type", "application/json");

			RequestConfig.Builder requestBuilder = RequestConfig.custom();
			requestBuilder.setConnectTimeout(5000);
			requestBuilder.setConnectionRequestTimeout(5000);
			requestBuilder.setSocketTimeout(5000);

			HttpClientBuilder httpClientBuilder = HttpClients.custom();
			httpClientBuilder.setDefaultRequestConfig(requestBuilder.build());

			HttpResponse response = httpClientBuilder.build().execute(request);

			ObjectMapper om = new ObjectMapper();
			LOGGER.info("response with SSL {}", om.writeValueAsString(response.getStatusLine().getStatusCode()));
			LOGGER.info("response with SSL {}", om.writeValueAsString(response.getStatusLine()));
			
			return new JSONParser().parse(IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8));

		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
