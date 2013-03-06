package utilities.logger;

import java.awt.Color;

public enum LogSource {
	ERROR(255, 0, 0),
	WARNING(255, 117, 0),
	APPLICATION_EVENT(255, 0, 255),
	SYNTHESIS_OUTPUT(10, 40, 255),
	SYNTHESIS_INFO(20, 100, 20),
	INTERPRETER_INFO(255, 100, 0),
	OD_BASE_INFO(0, 120, 255),
	OD_OBJECT_CREATION_INFO(0, 183, 255),
	OD_DFS_INFO(0, 183, 198),
	OD_QUERY_INFO(0, 143, 158),
	OD_COMPONENT_INFO(100, 255, 0),
	OD_NODE_BEHAVIOUR(255, 0, 255),
	DETERMINER_INFO(150, 140, 0),
	PHRASE_INFO(40, 200, 10),
	TAGGER_INFO(100, 20, 100),
	USER_INPUT(0, 100, 10),
	AUDIO_OUT(0, 255, 100),
	ARDUINO(100, 100, 100),
	EVENT_INFO(80, 255, 80),
	GOOGLE_SPEECH_INFO(100, 255, 0),
	FILE_ACCES(100, 100, 0);
	private final Color color;

	LogSource(int r, int g, int b) {
		color = new Color(r, g, b);
	}

	public Color getColor() {
		return color;
	}
}
