package net.sprakle.homeAutomation.externalSoftware.software.speechRecognition;

import net.sprakle.homeAutomation.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.externalSoftware.software.speechRecognition.SpeechRecognition;
import net.sprakle.homeAutomation.externalSoftware.software.speechRecognition.supporting.SpeechRecognitionObserver;

class SpeechRecognitionInactive implements SpeechRecognition {
	@Override
	public void addObserver(SpeechRecognitionObserver observer) {
	}

	@Override
	public void removeObserver(SpeechRecognitionObserver observer) {
	}
}
