package speech.interpretation.utilities.intention.determiners;

import speech.interpretation.Phrase;

public interface Determiner {

	// returns the name of this determiner in human readable form
	public String getName();

	// returns true if the phrase applies to the determiner
	public Boolean determine(Phrase phrase);;

	// execute the determiner's intention
	public void execute(Phrase phrase);
}
