package net.sprakle.homeAutomation.externalSoftware.software.speechRecognition;

import net.sprakle.homeAutomation.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.externalSoftware.software.SoftwareInterface;
import net.sprakle.homeAutomation.externalSoftware.software.SoftwareInterfaceFactory;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class SpeechRecognitionFactory implements SoftwareInterfaceFactory{

	private final Logger logger;

	public SpeechRecognitionFactory(Logger logger) {
		this.logger = logger;
	}

	@Override
	public SoftwareInterface getActiveSoftware() {
		return new SpeechRecognitionActive(logger);
	}

	@Override
	public SoftwareInterface getInactiveSoftware() {
		return new SpeechRecognitionInactive();
	}

	@Override
	public SoftwareName getSoftwareName() {
		return SoftwareName.SPEECH_RECOGNITION;
	}
}
