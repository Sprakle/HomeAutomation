package net.sprakle.homeAutomation.externalSoftware.software.weather.supporting;

import java.util.HashMap;

import net.sprakle.homeAutomation.main.Info;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.dom4j.Element;

public class CurrentWeather extends WeatherTimeframe {

	public CurrentWeather(Logger logger, Element e) {
		super(logger, e);

		this.logger = logger;
	}

	@Override
	protected HashMap<ConditionType, Condition> makeConditions(Element e) {
		HashMap<ConditionType, Condition> conditions = new HashMap<ConditionType, Condition>();

		String windspeedKey = getWindspeedKey();
		String tempKey = getTempKey();

		Condition image = new Condition(logger, e.elementText("weatherIconUrl"));
		Condition description = new Condition(logger, e.elementText("weatherDesc"));
		Condition windspeed = new Condition(logger, getInt(e.element(windspeedKey)));
		Condition windDirection = new Condition(logger, e.elementText("winddir16Point"));
		Condition precipitation = new Condition(logger, getDouble(e.element("precipMM")));
		Condition temp = new Condition(logger, getInt(e.element(tempKey)));
		Condition humidity = new Condition(logger, getInt(e.element("humidity")));
		Condition visibility = new Condition(logger, getInt(e.element("visibility")));
		Condition pressure = new Condition(logger, getInt(e.element("pressure")));
		Condition cloudcover = new Condition(logger, getInt(e.element("cloudcover")));

		conditions.put(ConditionType.IMAGE, image);
		conditions.put(ConditionType.DESCRIPTION, description);
		conditions.put(ConditionType.WINDSPEED, windspeed);
		conditions.put(ConditionType.WIND_DICECTION, windDirection);
		conditions.put(ConditionType.PRECIPITATION, precipitation);
		conditions.put(ConditionType.TEMP, temp);
		conditions.put(ConditionType.HUMIDITY, humidity);
		conditions.put(ConditionType.VISIBILITY, visibility);
		conditions.put(ConditionType.PRESSURE, pressure);
		conditions.put(ConditionType.CLOUDCOVER, cloudcover);

		return conditions;
	}

	private String getWindspeedKey() {
		String windspeedKey = null;

		switch (Info.getUnits()) {
			case IMPERIAL:
				windspeedKey = "windspeedMiles";
				break;
			case METRIC:
				windspeedKey = "windspeedKmph";
				break;
		}

		return windspeedKey;
	}

	private String getTempKey() {
		String tempKey = null;

		switch (Info.getUnits()) {
			case IMPERIAL:
				tempKey = "temp_F";
				break;

			case METRIC:
				tempKey = "temp_C";
				break;
		}

		return tempKey;
	}
}
