package net.sprakle.homeAutomation.utilities.personality.dynamicResponse;

public enum Response {
	ACTIVATED(new String[] { "Turned on", "Activated", "Enabled" }),
	DEACTIVATED(new String[] { "Turned off", "De-activated", "Disabled" }),

	I_DIDNT_UNDERSTAND(new String[] { "I'm sorry, I didn't understand that", "What did you say", "Would you repeat that" }),
	TOO_AMBIGUOUS(new String[] { "Could you be more specific", "Thats to ambiguous", "Please be more specific" }),

	I_COULD_NOT(new String[] { "I was unable to", "I'm sorry, I couldn't", "I couldn't" });

	private final String[] response;

	Response(String[] s) {
		response = s;
	}

	public String[] getResponses() {
		return response;
	}
}
