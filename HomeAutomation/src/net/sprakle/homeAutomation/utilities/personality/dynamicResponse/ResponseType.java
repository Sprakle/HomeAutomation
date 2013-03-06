package net.sprakle.homeAutomation.utilities.personality.dynamicResponse;

public enum ResponseType {
	ACTIVATED(new String[] { "Turned on", "Activated", "Enabled" }),
	DEACTIVATED(new String[] { "Turned off", "De-activated", "Disabled" }),

	I_DIDNT_UNDERSTAND(new String[] { "I'm sorry, I didn't understand that", "What did you say", "Would you repeat that" }),
	TOO_AMBIGUOUS(new String[] { "Could you be more specific", "Thats to ambiguous", "Please be more specific" });

	private final String[] response;

	ResponseType(String[] s) {
		response = s;
	}

	public String[] getResponses() {
		return response;
	}
}
