/*
 * This class is used to add dynamic responses to Walter by randomising his responses
 */

package utilities.personality.dynamicResponse;

public class DynamicResponder {

	// replies with a random reply from the given category
	public static String reply(ResponseType type) {
		String response = "error";
		String[] possibleResponses = type.getResponses();

		// create a random number that corresponds to an index in the possibleResponses array
		int chosen = (int) Math.round(Math.random() * (possibleResponses.length - 1));

		response = possibleResponses[chosen];

		return response;
	}
}
