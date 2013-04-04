package net.sprakle.homeAutomation.utilities.externalSoftware.software.rhythmbox;

public enum SystemCommand {
	PLAY_TRACK("--play-uri"),
	ENQUEUE("--enqueue"); // add song to queue

	private final String COMMAND;
	SystemCommand(String command) {
		this.COMMAND = command;
	}

	public String getCommand() {
		return COMMAND;
	}
}
