package net.sprakle.homeAutomation.interaction.weather;

public enum ConditionType {
	// forecast & current
	IMAGE,
	DESCRIPTION,
	WINDSPEED,
	WIND_DICECTION,
	PRECIPITATION,

	// forecast specific
	MAX_TEMP,
	MIN_TEMP,

	// current specific
	TEMP,
	HUMIDITY,
	VISIBILITY,
	PRESSURE,
	CLOUDCOVER;
}
