package speech.interpretation.utilities.intention.determiners;

import speech.interpretation.Phrase;
import speech.interpretation.module.ModuleManager;
import speech.interpretation.module.ModuleManager.ClaimResponse;
import utilities.logger.LogSource;
import utilities.logger.Logger;

public class ModuleInterpreted implements Determiner {

	Logger logger;
	ModuleManager mm;

	ModuleInterpreted(Logger logger, ModuleManager mm) {
		this.logger = logger;
		this.mm = mm;
	}

	String name = "Module Interpretedd";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Boolean determine(Phrase phrase) {
		ClaimResponse response = mm.submitForClaiming(phrase);

		if (response.notClaimed) {
			return false;

		} else if (response.toManyClaimed) {
			return false;
		} else { // if everything worked out
			return true;
		}
	}

	@Override
	public void execute(Phrase phrase) {

		ClaimResponse response = mm.submitForClaiming(phrase);

		if (!response.notClaimed && !response.toManyClaimed) {
			response.module.execute(phrase);
		}

		logger.log("MODULE INTERPRETATION", LogSource.DETERMINER_INFO, 2);
	}
}
