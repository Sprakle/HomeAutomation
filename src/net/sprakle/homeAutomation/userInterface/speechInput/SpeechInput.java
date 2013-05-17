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
    @SuppressWarnings("FieldCanBeLocal")
    private final String pre = "config/speech_recognition/";

    private final Logger logger;

	private final GoogleSpeech gs;

	// shows the user what speech was detected
    private final Visual visual;
	private final int DISPLAY_TIME = Config.getInt("config/speech_recognition/visual_display_time");
	private int displayTimeRemaining = 0;

	public SpeechInput(Logger logger) {
		this.logger = logger;

        float RECORD_END_TIME_THRESHOLD = Config.getInt(pre + "record_end_time_threshold");
        float RECORD_END_RMS_THRESHOLD = Config.getInt(pre + "record_end_rms_threshold");
        float RECORD_START_TIME_THRESHOLD = Config.getInt(pre + "record_start_time_threshold");
        float RECORD_START_RMS_THRESHOLD = Config.getInt(pre + "record_start_rms_threshold");
        RecordThresholds thresholds = new RecordThresholds(
                RECORD_START_RMS_THRESHOLD,
                RECORD_START_TIME_THRESHOLD,
                RECORD_END_RMS_THRESHOLD,
                RECORD_END_TIME_THRESHOLD);

		Output output = new Output();
        int PREPEND_BYTES = Config.getInt(pre + "prepend_bytes");
        int SAMPLE_RATE = Config.getInt(pre + "sample_rate");
        gs = new GoogleSpeech(output, thresholds, SAMPLE_RATE, PREPEND_BYTES);
		gs.listenForSpeech();

		LogicTimer.getLogicTimer().addObserver(this);

		visual = new Visual();
	}

	private class Output implements net.sprakle.jGoogleSpeech.Logger {

		@Override
		public void log(boolean criticalError, String info) {

			if (criticalError)
				logger.log(info, LogSource.ERROR, LogSource.GOOGLE_SPEECH_INFO, 1);
			else
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

		// check if visual should still be displaying
		if (displayTimeRemaining > 0) {
			displayTimeRemaining--;

			if (displayTimeRemaining == 0) {
				visual.setText("");
			}
		}
	}

	private void updateObservers(String input) {
		UserSpeechRecievedEvent sre = new UserSpeechRecievedEvent(input);

		EventManager em = EventManager.getInstance(logger);
		em.call(EventType.USER_SPEECH_RECIEVED, sre);

		setVisual(input);
	}

	private void setVisual(String text) {
		visual.setText(text);
		displayTimeRemaining = DISPLAY_TIME;
	}
}
