package net.sprakle.homeAutomation.externalSoftware.software.weather;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import net.sprakle.homeAutomation.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.externalSoftware.software.weather.supporting.Client;
import net.sprakle.homeAutomation.externalSoftware.software.weather.supporting.CurrentWeather;
import net.sprakle.homeAutomation.externalSoftware.software.weather.supporting.Forecast;
import net.sprakle.homeAutomation.main.Config;
import net.sprakle.homeAutomation.timer.LogicTimer;
import net.sprakle.homeAutomation.timer.interfaces.observer.LogicTimerObserver;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

class InternetWeatherActive implements InternetWeather, LogicTimerObserver {

	private final Logger logger;

	private final String API_KEY = Config.getString("config/weather/api_key");
	private final int UPDATE_FREQUENCY = Config.getInt("config/weather/update_frequency");
	private final String LOCATION = Config.getString("config/weather/location");

	private List<Forecast> forecasts;
	private CurrentWeather currentWeather;

	private long lastUpdate;

	public InternetWeatherActive(Logger logger) {
		this.logger = logger;

		updateWeather();

		LogicTimer.getLogicTimer().addObserver(this);
	}

	private void updateWeather() {
		logger.log("Updating weather", LogSource.WEATHER, 2);
		String URIString = "http://api.worldweatheronline.com/free/v1/weather.ashx?q=" + LOCATION + "&format=xml&num_of_days=7&key=" + API_KEY;

		Client client = new Client(logger);
		URI uri = null;
		try {
			uri = new URI(URIString);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		Document doc = client.getWeather(uri);
		Element root = doc.getRootElement();

		forecasts = new ArrayList<>();
		currentWeather = null;

		for (int i = 0, size = root.nodeCount(); i < size; i++) {
			Node node = root.node(i);
			if (node instanceof Element) {
				Element e = (Element) node;

				if (e.getName().equals("current_condition")) {
					currentWeather = new CurrentWeather(logger, e);
				}

				if (e.getName().equals("weather")) {
					Forecast forecast = new Forecast(logger, e);
					forecasts.add(forecast);
				}
			}
		}

		logger.log("Completed updating weather", LogSource.WEATHER, 2);
		lastUpdate = System.currentTimeMillis();
	}

	@Override
	public void advanceLogic() {
		long currentTime = System.currentTimeMillis();
		long difference = currentTime - lastUpdate;

		long minutes = difference / 60000;
		if (minutes >= UPDATE_FREQUENCY)
			updateWeather();
	}

	public List<Forecast> getForecasts() {
		return forecasts;
	}

	@Override
	public Forecast getForecast(int num) {
		return forecasts.get(num);
	}

	@Override
	public CurrentWeather getCurrentWeather() {
		return currentWeather;
	}
}
