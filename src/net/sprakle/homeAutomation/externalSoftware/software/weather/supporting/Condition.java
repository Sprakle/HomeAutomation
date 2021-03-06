package net.sprakle.homeAutomation.externalSoftware.software.weather.supporting;

import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class Condition {

	private final Logger logger;

	private int intValue;
	private String stringValue;
	private double doubleValue;

	private final ReturnType type;

	public Condition(Logger logger, int intValue) {
		this.logger = logger;
		this.intValue = intValue;
		type = ReturnType.INTEGER;
	}

	public Condition(Logger logger, String stringValue) {
		this.logger = logger;
		this.stringValue = stringValue;
		type = ReturnType.STRING;
	}

	public Condition(Logger logger, double doubleValue) {
		this.logger = logger;
		this.doubleValue = doubleValue;
		type = ReturnType.DOUBLE;
	}

	public int getAsInt() {
		int result = -1;

		switch (type) {
			case DOUBLE:
				thowUnconvertable();
				break;

			case INTEGER:
				result = intValue;
				break;

			case STRING:
				thowUnconvertable();
				break;
		}
		return result;
	}

	public String getAsString() {
		String result = null;

		switch (type) {
			case DOUBLE:
				result = Double.toString(doubleValue);
				break;
			case INTEGER:
				result = Integer.toString(intValue);
				break;
			case STRING:
				result = stringValue;
				break;
		}
		return result;
	}

	public double getAsDouble() {
		double result = -1;

		switch (type) {
			case DOUBLE:
				result = doubleValue;
				break;
			case INTEGER:
				result = intValue;
				break;
			case STRING:
				thowUnconvertable();
				break;
		}
		return result;
	}

	private void thowUnconvertable() {
		logger.log("Unable to convert condition value to requested value", LogSource.ERROR, LogSource.WEATHER, 1);
	}

	enum ReturnType {
		INTEGER,
		STRING,
		DOUBLE
    }
}
