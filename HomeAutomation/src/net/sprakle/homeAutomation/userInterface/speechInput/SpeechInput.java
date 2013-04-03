package net.sprakle.homeAutomation.userInterface.speechInput;

import net.sprakle.homeAutomation.events.EventManager;
import net.sprakle.homeAutomation.events.EventType;
import net.sprakle.homeAutomation.main.Config;
import net.sprakle.homeAutomation.timer.LogicTimer;
import net.sprakle.homeAutomation.timer.interfaces.observer.LogicTimerObserver;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.jGoogleSpeech.GoogleSpeech;
import net.sprakle.jGoogleSpeech.RecordThresholds;

public class SpeechInput implements LogicTimerObserver {

	// hold path in xml file for easy reading
	String pre = "config/speech_recognition/";

	// RMS of audio that must be above to begin recording
	private final float RECORD_START_RMS_THRESHOLD = Config.getInt(pre + "record_start_rms_threshold");
	private final float RECORD_START_TIME_THRESHOLD = Config.getInt(pre + "record_start_time_threshold"); // does nothing yet, recording starts as soon as threshold is passed

	// RMS of audio that must be less than to end recording
	private final float RECORD_END_RMS_THRESHOLD = Config.getInt(pre + "record_end_rms_threshold");
	private final float RECORD_END_TIME_THRESHOLD = Config.getInt(pre + "record_end_time_threshold"); // milliseconds RMS must be below threshold for

	// test have shown that changing this does not significantly change the time taken to process speech
	private final int SAMPLE_RATE = Config.getInt(pre + "sample_rate");

	Logger logger;

	GoogleSpeech gs;

	public SpeechInput(Logger logger) {
		this.logger = logger;

		RecordThresholds thresholds = new RecordThresholds(
				RECORD_START_RMS_THRESHOLD,
				RECORD_START_TIME_THRESHOLD,
				RECORD_END_RMS_THRESHOLD,
				RECORD_END_TIME_THRESHOLD);

		Output output = new Output();
		gs = new GoogleSpeech(output, thresholds, SAMPLE_RATE);
		gs.listenForSpeech();

		LogicTimer.getLogicTimer().addObserver(this);
	}

	class Output implements net.sprakle.jGoogleSpeech.Logger {

		@Override
		public void log(String info) {
			logger.log(info, LogSource.GOOGLE_SPEECH_INFO, 3);
		}
	}

	@Override
	public void advanceLogic() {
		String s = gs.getSpeech();
		if (s != null) {
			logger.log(s, LogSource.USER_INPUT, 1);

			updateObservers(s);

			// continue listening
			gs.listenForSpeech();
		}
	}

	private void updateObservers(String input) {
		UserSpeechRecievedEvent sre = new UserSpeechRecievedEvent(input);

		EventManager em = EventManager.getInstance(logger);
		em.call(EventType.USER_SPEECH_RECIEVED, sre);
	}
}
