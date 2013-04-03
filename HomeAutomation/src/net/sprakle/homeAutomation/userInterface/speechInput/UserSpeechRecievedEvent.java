package net.sprakle.homeAutomation.userInterface.speechInput;

import net.sprakle.homeAutomation.events.Event;

public class UserSpeechRecievedEvent extends Event {
	public final String speech;

	public UserSpeechRecievedEvent(String speech) {
		this.speech = speech;
	}
}
