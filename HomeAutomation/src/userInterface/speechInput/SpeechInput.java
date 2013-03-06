package userInterface.speechInput;

import java.util.ArrayList;

import net.sprakle.jGoogleSpeech.GoogleSpeech;
import net.sprakle.jGoogleSpeech.RecordThresholds;
import timer.LogicTimer;
import timer.interfaces.observer.LogicTimerObserver;
import utilities.logger.LogSource;
import utilities.logger.Logger;

public class SpeechInput implements LogicTimerObserver {

	// RMS of audio that must be above to begin recording
	private final float RECORD_START_RMS_THRESHOLD = 10;
	private final float RECORD_START_TIME_THRESHOLD = 100; // does nothing yet, recording starts as soon as threshold is passed

	// RMS of audio that must be less than to end recording
	private final float RECORD_END_RMS_THRESHOLD = 2;
	private final float RECORD_END_TIME_THRESHOLD = 250; // milliseconds RMS must be below threshold for

	// test have shown that changing this does not significantly change the time taken to process speech
	private final int SAMPLE_RATE = 44100;

	Logger logger;

	GoogleSpeech gs;

	ArrayList<SpeechInputObserver> observers;

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

		observers = new ArrayList<SpeechInputObserver>();

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

	public void addObserver(SpeechInputObserver observer) {
		observers.add(observer);
	}

	public void removeObserver(SpeechInputObserver observer) {
		observers.remove(observer);
	}

	private void updateObservers(String input) {
		for (SpeechInputObserver sio : observers) {
			sio.speechInputUpdate(input);
		}
	}
}
