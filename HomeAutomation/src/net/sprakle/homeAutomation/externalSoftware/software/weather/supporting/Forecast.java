package net.sprakle.homeAutomation.externalSoftware.software.weather.supporting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import net.sprakle.homeAutomation.main.Info;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.dom4j.Element;

public class Forecast extends WeatherTimeframe {

	private Element element;

	private Date date;
	private String dayOfWeek;

	public Forecast(Logger logger, Element e) {
		super(logger, e);

		this.logger = logger;
		this.element = e;

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String forecastDateString = element.elementText("date");
		Date forecastDate = null;
		try {
			forecastDate = format.parse(forecastDateString);
		} catch (ParseException e1) {
			logger.log("Unable to parse date from weather API", LogSource.ERROR, LogSource.WEATHER, 1);
		}

		dayOfWeek = makeDayOfWeekName(forecastDate);
	}

	public Date getDate() {
		return date;
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	@Override
	protected HashMap<ConditionType, Condition> makeConditions(Element e) {
		HashMap<ConditionType, Condition> conditions = new HashMap<ConditionType, Condition>();

		String windspeedKey = getWindspeedKey();
		String tempMaxKey = "tempMax" + getTempKey();
		String tempMinKey = "tempMin" + getTempKey();

		Condition image = new Condition(logger, e.elementText("weatherIconUrl"));
		Condition description = new Condition(logger, e.elementText("weatherDesc"));
		Condition windspeed = new Condition(logger, getInt(e.element(windspeedKey)));
		Condition windDirection = new Condition(logger, e.elementText("winddir16Point"));
		Condition precipitation = new Condition(logger, getDouble(e.element("precipMM")));
		Condition maxTemp = new Condition(logger, getInt(e.element(tempMaxKey)));
		Condition minTemp = new Condition(logger, getInt(e.element(tempMinKey)));

		conditions.put(ConditionType.IMAGE, image);
		conditions.put(ConditionType.DESCRIPTION, description);
		conditions.put(ConditionType.WINDSPEED, windspeed);
		conditions.put(ConditionType.WIND_DICECTION, windDirection);
		conditions.put(ConditionType.PRECIPITATION, precipitation);
		conditions.put(ConditionType.MAX_TEMP, maxTemp);
		conditions.put(ConditionType.MIN_TEMP, minTemp);

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
				tempKey = "F";
				break;
			case METRIC:
				tempKey = "C";
				break;
		}

		return tempKey;
	}

	private String makeDayOfWeekName(Date forecastDate) {
		String name = null;

		Calendar cal = Calendar.getInstance();

		// forecast day
		cal.setTime(forecastDate);
		int forecastDayNum = cal.get(Calendar.DAY_OF_WEEK);

		// current day
		cal.setTime(new Date());
		int currentDayNum = cal.get(Calendar.DAY_OF_WEEK);

		if (forecastDayNum == currentDayNum) {
			name = "today";
		} else {
			name = dayNameFromInt(forecastDayNum);
		}

		return name;
	}

	private String dayNameFromInt(int dayNum) {
		String name = null;

		switch (dayNum) {
			case 1:
				name = "sunday";
				break;

			case 2:
				name = "monday";
				break;

			case 3:
				name = "tuesday";
				break;

			case 4:
				name = "wednesday";
				break;

			case 5:
				name = "thursday";
				break;

			case 6:
				name = "friday";
				break;

			case 7:
				name = "saturday";
				break;

			default:
				logger.log("Unable to name day based on number", LogSource.ERROR, LogSource.WEATHER, 1);
				break;
		}

		return name;
	}
}
