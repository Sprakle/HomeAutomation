package net.sprakle.homeAutomation.externalSoftware.software.weather.supporting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.dom4j.Element;

abstract class WeatherTimeframe {

	Logger logger;

	private final HashMap<ConditionType, Condition> conditions;

	WeatherTimeframe(Logger logger, Element e) {
		this.logger = logger;

		conditions = makeConditions(e);
	}

	protected abstract HashMap<ConditionType, Condition> makeConditions(Element e);

	public int getConditionAsInt(ConditionType ct) {
		return conditions.get(ct).getAsInt();
	}

	public String getConditionAsString(ConditionType ct) {
		return conditions.get(ct).getAsString();
	}

	public double getConditionAsDouble(ConditionType ct) {
		return conditions.get(ct).getAsDouble();
	}

	protected Date getDate(Element e) {
		String dateString = e.getStringValue();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = formatter.parse(dateString);
		} catch (ParseException e1) {
			logger.log("Unable to parse date from weather XML", LogSource.ERROR, LogSource.WEATHER, 1);
		}
		return date;
	}

	int getInt(Element e) {
		String intString = e.getStringValue();
		int result = Integer.parseInt(intString);
		return result;
	}

	double getDouble(Element e) {
		String doubleString = e.getStringValue();
		double result = Double.parseDouble(doubleString);
		return result;
	}
}
