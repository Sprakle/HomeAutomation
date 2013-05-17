package net.sprakle.homeAutomation.externalSoftware.software.weather;

import net.sprakle.homeAutomation.externalSoftware.software.SoftwareInterface;
import net.sprakle.homeAutomation.externalSoftware.software.weather.supporting.CurrentWeather;
import net.sprakle.homeAutomation.externalSoftware.software.weather.supporting.Forecast;

public interface InternetWeather extends SoftwareInterface {

	/**
	 * 
	 * @param num
	 *            the day to get the forecast of. 0 is today, 1 is tomorrow, etc
	 * @return
	 */
	public Forecast getForecast(int num);

	public CurrentWeather getCurrentWeather();
}
