package net.sprakle.homeAutomation.interpretation.tagger.tags;

public enum TagType {
	NUMBER, // NOT defined in the tagList file, only applied later by the Tagger
	NTH_NUMBER, // NOT defined in the taglist file, only applied latter by the Tagger (1st, second, 3rd, etc)
	UNKOWN_TEXT, // text that has not been tagged
	GENERAL,
	POWER_OPTION, // EX: "Activate".
	OD_OBJECT, // EX: "kitchen", "heater"
	NODE,
	QUESTION, // EX: "what", "who". Will likely result in a RQD phrase
	SETTER, // set to a specific value. Will contain an integer to indicate the value
	UNIT, // degrees, percent, meter, etc.
	TIME_CHANGE, // next, last, etc 
	MEDIA, // commands to control music and other media
	POSSESSION, // by (Maybe change this?)
	PLAYBACK,
	INTERNALS,
	MATH_TERM,
	MATH_OPERATOR,
	MATH_FUNCTION,
	RELATIVE_DAY,
	DAY,
	WEATHER_CONDITION,
	GENERAL_COMMAND, // commands of areas that are not large enough to get their own tagtype, such spell
	FILE_CONTROL,
	DECISION, // yes/no
	SCHEDULE,
	TIME, // seconds, minutes, days, etc. in milliseconds
	SHORTHAND_TIME, // couple, few, several, etc
	FRACTION,
	PART_OF_DAY, // morning, afternoon, etc
	AM_PM,
	MONTH,
}
