package net.sprakle.homeAutomation.externalSoftware.software.speechRecognition;

import net.sprakle.homeAutomation.externalSoftware.software.SoftwareInterface;
import net.sprakle.homeAutomation.externalSoftware.software.speechRecognition.supporting.SpeechRecognitionObserver;

public interface SpeechRecognition extends SoftwareInterface {
	public void addObserver(SpeechRecognitionObserver observer);
	public void removeObserver(SpeechRecognitionObserver observer);
}
