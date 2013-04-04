package net.sprakle.homeAutomation.utilities.externalSoftware.software.rhythmbox;

public enum PlaybackCommand {
	PLAY("--play"),
	PAUSE("--pause"),
	NEXT("--next"),
	BACK("--previous");

	private final String COMMAND;
	PlaybackCommand(String command) {
		this.COMMAND = command;
	}

	public String getCommand() {
		return COMMAND;
	}
}
