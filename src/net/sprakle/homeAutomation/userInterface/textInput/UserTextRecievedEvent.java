package net.sprakle.homeAutomation.userInterface.textInput;

import net.sprakle.homeAutomation.events.Event;

public class UserTextRecievedEvent extends Event {
	public final String speech;

	public UserTextRecievedEvent(String speech) {
		this.speech = speech;
	}
}
