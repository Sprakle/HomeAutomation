package net.sprakle.homeAutomation.externalSoftware.software.weather;

import net.sprakle.homeAutomation.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.externalSoftware.software.weather.supporting.CurrentWeather;
import net.sprakle.homeAutomation.externalSoftware.software.weather.supporting.Forecast;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

class InternetWeatherInactive implements InternetWeather {

	private final Logger logger;

	public InternetWeatherInactive(Logger logger) {
		this.logger = logger;
	}

	@Override
	public SoftwareName getSoftwareName() {
		return SoftwareName.INTERNET_WEATHER;
	}

	@Override
	public Forecast getForecast(int num) {
		logger.log("Unable to create inactive forecast; it has not been programmed yet", LogSource.ERROR, LogSource.EXTERNAL_SOFTWARE, 1);
		return null;
	}

	@Override
	public CurrentWeather getCurrentWeather() {
		logger.log("Unable to create inactive current weather; it has not been programmed yet", LogSource.ERROR, LogSource.EXTERNAL_SOFTWARE, 1);
		return null;
	}

}
