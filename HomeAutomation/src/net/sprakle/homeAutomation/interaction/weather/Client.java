package net.sprakle.homeAutomation.interaction.weather;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

class Client {

	private Logger logger;

	private HttpClient httpClient;

	public Client(Logger logger) {
		this.logger = logger;

		httpClient = new DefaultHttpClient();
	}

	public Document getWeather(URI uri) {
		Document doc = null;

		HttpPost httppost = new HttpPost(uri);
		HttpResponse response = null;
		try {
			response = httpClient.execute(httppost);
		} catch (IOException e) {
			logger.log("Unable to get weather from url", LogSource.ERROR, LogSource.WEATHER, 1);
		}

		HttpEntity entity = response.getEntity();
		InputStream is = null;

		try {
			is = entity.getContent();
		} catch (IllegalStateException | IOException e) {
			logger.log("Unable to get weather from url", LogSource.ERROR, LogSource.WEATHER, 1);
		}

		SAXReader reader = new SAXReader();
		try {
			doc = reader.read(is);
		} catch (DocumentException e) {
			logger.log("Unable to read weather XML file", LogSource.ERROR, LogSource.WEATHER, 1);
		}

		return doc;
	}
}
