package net.sprakle.homeAutomation.interpretation.tagger.tags;

public enum TagType {

	// IDEA: find a way to consolidate this + tagList into one file
	POWER_OPTION, // EX: "Activate". // IDEA: make '-1' toggle it
	OD_OBJECT, // EX: "kitchen", "heater"
	NODE,
	QUESTION, // EX: "what", "who". Will likely result in a RQD phrase
	SETTER, // set to a specific value. Will contain an integer to indicate the value
	NUMBER, // NOT defined in the tagList file, only applied later by the Tagger
	UNIT; // degrees, percent, meter, etc.
}
